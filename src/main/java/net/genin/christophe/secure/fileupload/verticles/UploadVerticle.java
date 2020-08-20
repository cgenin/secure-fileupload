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
import net.genin.christophe.secure.fileupload.domain.service.UploadFiles;
import net.genin.christophe.secure.fileupload.domain.entities.Event;
import net.genin.christophe.secure.fileupload.domain.valueobject.EventState;
import net.genin.christophe.secure.fileupload.domain.entities.UploadEvent;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.domain.adapters.SaveUploadAdapter;
import net.genin.christophe.secure.fileupload.domain.adapters.UploadEventAdapter;

public class UploadVerticle extends AbstractVerticle implements UploadEventAdapter, FileAdapter, SaveUploadAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(UploadVerticle.class);
    public static final String UPLOAD = UploadVerticle.class.getName() + ".upload";


    @Override
    public void start() {
        vertx.eventBus().<JsonObject>consumer(UPLOAD, msg ->
                Single.just(msg)
                        .map(Message::body)
                        .map(j -> j.mapTo(UploadEvent.class))
                        .flatMap(u -> new UploadFiles(u).run(this, this, this))
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
    public void saveAndNotify(UploadEvent uploadEvent) {
        final JsonObject value = JsonObject.mapFrom(uploadEvent);
        final String key = EventState.uploaded + ":" + uploadEvent.getIdApplication();
        final JsonObject msg = new JsonObject().put("key", key).put("value", value);
        vertx.eventBus().rxRequest(DbVerticle.SAVE, msg)
                .subscribe(
                        g -> LOG.info("Saved " + key),
                        t -> LOG.error("Error in saving " + value.encode(), t)
                );
    }
}
