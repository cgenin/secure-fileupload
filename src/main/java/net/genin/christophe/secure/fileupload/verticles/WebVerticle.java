package net.genin.christophe.secure.fileupload.verticles;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import net.genin.christophe.secure.fileupload.Api;
import net.genin.christophe.secure.fileupload.Jsons;
import net.genin.christophe.secure.fileupload.models.Upload;
import net.genin.christophe.secure.fileupload.models.UploadedFile;

import java.util.List;
import java.util.stream.Collectors;

public class WebVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(WebVerticle.class);

    @Override
    public void start() {
        final String host = config().getString("HOST", "localhost");
        final int port = config().getInteger("PORT", 8000);

        final Router router = Router.router(vertx)
                .mountSubRouter("/upload", upload())
                .mountSubRouter("/api", new Api(vertx).build())
                .mountSubRouter("/static", staticFiles());

        vertx
                .createHttpServer()
                .requestHandler(router)
                .rxListen(port, host)
                .subscribe(
                        server -> LOG.info("web server started at http://" + host + ":" + port + "/"),
                        t -> {
                            LOG.error("Failed to listen on port +" + port, t);
                            vertx.close();
                        }
                );
    }

    private Router staticFiles() {
        final Router router = Router.router(vertx);
        router.route("/*").handler(StaticHandler.create());
        return router;
    }

    private Router upload() {
        final String uploadDirectory = config().getString("UPLOAD_DIRECTORY", "/tmp");
        LOG.info("Upload directory : " + uploadDirectory);
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/:id")
                .handler(rc -> {
                    final String id = rc.pathParam("id");
                    final List<UploadedFile> fileUploads = rc.fileUploads()
                            .stream()
                            .map(f -> new UploadedFile(f.name(), f.uploadedFileName(), f.fileName(), f.contentType(), f.contentTransferEncoding(), f.contentTransferEncoding(), f.size()))
                            .collect(Collectors.toList());
                    final JsonObject msg = JsonObject.mapFrom(new Upload(id, fileUploads));
                    vertx.eventBus().<JsonObject>rxRequest(UploadVerticle.UPLOAD, msg)

                            .subscribe(m -> {
                                        final JsonObject body = m.body();
                                        final Integer status = body.getInteger("status", 404);
                                        new Jsons(rc).withStatus(status).json(body);
                                    },
                                    throwable -> {
                                        LOG.error("Error in uploading", throwable);
                                        rc.response().setStatusCode(500).end();
                                    });

                });
        return router;
    }
}
