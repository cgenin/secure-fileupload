package net.genin.christophe.secure.fileupload;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;
import net.genin.christophe.secure.fileupload.verticles.*;


public class Main {


    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        final ConfigRetriever configRetriever = Utils.getConfigRetriever(vertx);
        configRetriever.getConfig(r -> {
            final JsonObject json = r.result();
            new Deployments(vertx, json)
                    .deploy(new RegisterVerticle(), new DbVerticle(), new UploadVerticle(),
                            new WebVerticle())
                    .subscribe();

        });

    }

}
