package net.genin.christophe.secure.fileupload;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

public class Deployments {

    private final Vertx vertx;
    private final JsonObject config;

    public Deployments(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.config = config;
    }

    public Single<JsonArray> deploy(Verticle... verticles) {
        return Observable.fromArray(verticles)
                .flatMap(v -> vertx.rxDeployVerticle(v, new DeploymentOptions().setConfig(config))
                        .map(m-> new JsonObject().put("verticle", v.getClass().getName()).put("id", m))
                        .toObservable())
                .reduce(new JsonArray(), JsonArray::add);
    }
}
