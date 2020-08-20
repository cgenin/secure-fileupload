package net.genin.christophe.secure.fileupload.verticles;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import net.genin.christophe.secure.fileupload.Jsons;
import net.genin.christophe.secure.fileupload.domain.entities.Upload;
import net.genin.christophe.secure.fileupload.domain.entities.UploadedFile;
import net.genin.christophe.secure.fileupload.web.Api;
import net.genin.christophe.secure.fileupload.web.Templates;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(WebVerticle.class);
    public static final Pattern WEB_COMPONENT_PATH = Pattern.compile("/static/wc/");

    @Override
    public void start() {
        final String host = config().getString("HOST", "localhost");
        final int port = config().getInteger("PORT", 8000);
        final Router router = Router.router(vertx)
                .mountSubRouter("/upload", upload())
                .mountSubRouter("/public", new Templates(vertx).build())
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
        router.route().path("/wc/*").handler(rc -> {
            final String path = WEB_COMPONENT_PATH.matcher(rc.request().path()).replaceAll("");
            vertx.eventBus().<Buffer>rxRequest(GzVerticle.GET, path)
                    .map(Message::body)
                    .subscribe(buffer -> {
                                String contentType = MimeMapping.getMimeTypeForFilename(path);
                                rc.response().putHeader(HttpHeaders.CONTENT_TYPE, contentType)
                                        .putHeader(HttpHeaders.CONTENT_ENCODING, "gzip")
                                        .end(io.vertx.reactivex.core.buffer.Buffer.newInstance(buffer));

                            },
                            t -> rc.next()
                    );

        });
        router.route("/*").handler(StaticHandler.create());
        return router;
    }

    private Router upload() {
        final String uploadDirectory = config().getString("UPLOAD_DIRECTORY", "/tmp");
        LOG.info("Upload directory : " + uploadDirectory);
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        Optional.ofNullable(config().getString("CORS_ORIGIN"))
                .ifPresent(cors -> router.route().handler(CorsHandler.create(cors)));
        router.options("/:id")
                .handler((rc)->new Jsons(rc).withStatus(200).json(new JsonObject()));
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
