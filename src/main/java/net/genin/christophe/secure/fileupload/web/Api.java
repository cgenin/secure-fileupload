package net.genin.christophe.secure.fileupload.web;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import net.genin.christophe.secure.fileupload.Jsons;
import net.genin.christophe.secure.fileupload.verticles.DbVerticle;
import net.genin.christophe.secure.fileupload.verticles.RegisterVerticle;

public class Api {
    private final Vertx vertx;

    public Api(Vertx vertx) {
        this.vertx = vertx;
    }

    public Router build() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        return router
                .mountSubRouter("/health", health())
                .mountSubRouter("/events", events());
    }

    private Router events() {
        final Router router = Router.router(vertx);
        router.post("/").handler(rc -> {
            final JsonObject query = rc.getBodyAsJson();
            vertx.eventBus().<JsonObject>rxRequest(RegisterVerticle.REGISTER_NEW, query)
                    .subscribe(
                            m -> {
                                final JsonObject body = m.body();
                                final String id = body.getString("id");
                                new Jsons(rc).json(
                                        body.put("html", "/public/" + id + ".html")
                                                .put("js", "/public/" + id + ".js")
                                );
                            },
                            throwable -> rc.response().setStatusCode(400).end()
                    );
        });
        return router;
    }

    private Router health() {
        final Router router = Router.router(vertx);
        router.get("/")
                .handler(rc ->
                        vertx.eventBus().<Boolean>rxRequest(DbVerticle.PING, "")
                                .map(Message::body)
                                .onErrorResumeNext(t -> Single.just(false))
                                .subscribe(b -> new Jsons(rc)
                                        .json(new JsonObject()
                                                .put("up", true)
                                                .put("db", b)
                                        )
                                )
                );
        return router;
    }
}
