package net.genin.christophe.secure.fileupload.verticles;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.Message;
import net.genin.christophe.secure.fileupload.Utils;
import net.genin.christophe.secure.fileupload.models.UploadFiles;
import net.genin.christophe.secure.fileupload.models.entities.Event;
import net.genin.christophe.secure.fileupload.models.entities.EventState;
import net.genin.christophe.secure.fileupload.models.entities.Upload;
import net.genin.christophe.secure.fileupload.models.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.SaveUploadAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.UploadEventAdapter;

public class UploadVerticle extends AbstractVerticle implements UploadEventAdapter, FileAdapter, SaveUploadAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(UploadVerticle.class);
    public static final String UPLOAD = UploadVerticle.class.getName() + ".upload";


    @Override
    public void start() {
        vertx.eventBus().<JsonObject>consumer(UPLOAD, msg ->
                Single.just(msg)
                        .map(Message::body)
                        .map(j -> j.mapTo(Upload.class))
                        .flatMap(u -> new UploadFiles(u).upload(this, this, this))
                        .subscribe(u -> {
                            final JsonObject entries = JsonObject.mapFrom(u);
                            msg.reply(entries);
                        }, t -> {
                            LOG.error("Error in upload", t);
                            msg.fail(500, "Error");
                        })
        );
    }

    @Override
    public Single<Event> findByIdApplication(String idApplication) {
        String key = EventState.registered.name() + ":" + idApplication;
        return vertx.eventBus().<JsonObject>rxRequest(DbVerticle.GET, key)
                .map(Message::body)
                .map(json -> json.mapTo(Event.class));
    }

    @Override
    public void delete(Event event) {
        String key = Utils.createKey(event);
        vertx.eventBus().rxRequest(DbVerticle.DELETE, key).subscribe();
    }

    @Override
    public Single<byte[]> readContentFile(String path) {
        return vertx.fileSystem().rxReadFile(path)
                .map(Buffer::getBytes);
    }

    @Override
    public void delete(String path) {
        vertx.fileSystem().rxDelete(path).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public Single<Boolean> write(String path, byte[] toByteArray) {
        return vertx.fileSystem().rxWriteFile(path, Buffer.buffer(toByteArray))
                .toSingle(() -> true);
    }

    @Override
    public void saveAndNotify(Upload upload) {
        final JsonObject value = JsonObject.mapFrom(upload);
        final String key = EventState.uploaded + ":" + upload.getIdApplication();
        final JsonObject msg = new JsonObject().put("key", key).put("value", value);
        vertx.eventBus().rxRequest(DbVerticle.SAVE, msg)
                .subscribe(
                        g -> LOG.info("Saved " + key),
                        t -> LOG.error("Error in saving " + value.encode(), t)
                );
    }
}
