package net.genin.christophe.secure.fileupload;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
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
                    .subscribe(m -> new Jsons(rc).json(m.body()),
                            throwable -> rc.response().setStatusCode(400).end()
                    );
        });
        return router;
    }

    private Router health() {
        final Router router = Router.router(vertx);
        router.get("/").handler(rc -> new Jsons(rc).json(new JsonObject().put("up", true)));
        return router;
    }
}
