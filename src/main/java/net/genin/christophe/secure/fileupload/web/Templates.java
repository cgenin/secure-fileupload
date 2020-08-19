package net.genin.christophe.secure.fileupload.web;

import io.reactivex.Single;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.TemplateHandler;
import io.vertx.reactivex.ext.web.templ.handlebars.HandlebarsTemplateEngine;
import net.genin.christophe.secure.fileupload.verticles.DbVerticle;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Templates {
    private static final Logger LOG = LoggerFactory.getLogger(Templates.class);
    public static final Pattern REFERER_REGEXP = Pattern.compile("(http[s]?://[^/]+)/");
    private final Vertx vertx;
    private final HandlebarsTemplateEngine engine;
    private final TemplateHandler templateHandler;

    public Templates(Vertx vertx) {
        this.vertx = vertx;
        this.engine = HandlebarsTemplateEngine.create(vertx);
        this.templateHandler = TemplateHandler.create(engine);
    }

    public Router build() {
        final Router router = Router.router(vertx);

        router.get("/:id.html").handler(rc -> {
            final String id = rc.pathParam("id");
            loadEvent(id)
                    .flatMap(json -> engine.rxRender(json, "templates/uploadform.html.hbs"))
                    .subscribe(
                            b -> rc.response()
                                    .putHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8")
                                    .end(b),
                            t -> {
                                LOG.error("error in rendering" + id, t);
                                rc.response().setStatusCode(400).end();
                            });

        });
        router.get("/:id.js").handler(rc -> {
            final String id = rc.pathParam("id");
            final String host = Optional.ofNullable(rc.request().getHeader("X-Forwarded-For"))
                    .flatMap(this::extractHost)
                    .orElseGet(() -> extractHost(rc.request().absoluteURI())
                            .orElseThrow(() -> new IllegalStateException("Impossible to extract host")));

            loadEvent(id)
                    .map(json -> json.put("host", host))
                    .flatMap(json -> engine.rxRender(json, "templates/upload.js.hbs"))
                    .onErrorResumeNext(t -> {
                        LOG.error("error in rendering" + id, t);
                        return engine.rxRender(new JsonObject().put("id", id), "templates/error.js.hbs");
                    })
                    .subscribe(
                            b -> rc.response()
                                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/javascript; charset=utf-8")
                                    .end(b),
                            t -> {
                                LOG.error("error in rendering" + id, t);
                                rc.fail(404);
                            });
        });


        router.route().handler(templateHandler);
        return router;
    }

    public Optional<String> extractHost(String h) {
        final Matcher matcher = REFERER_REGEXP.matcher(h);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.ofNullable(matcher.group(1));
    }

    private Single<JsonObject> loadEvent(String id) {
        return vertx.eventBus().<JsonObject>rxRequest(DbVerticle.GET, "registered:" + id)
                .map(Message::body)
                .map(event -> {
                    final String accept = event.getJsonArray("extensions").stream()
                            .map(Object::toString)
                            .reduce("", (acc, v) -> {
                                if (acc.isEmpty())
                                    return v;
                                return acc + "," + v;
                            });
                    return new JsonObject().put("id", id)
                            .put("accept", accept);
                });
    }

}
