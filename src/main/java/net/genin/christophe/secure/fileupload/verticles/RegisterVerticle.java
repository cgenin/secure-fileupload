package net.genin.christophe.secure.fileupload.verticles;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import net.genin.christophe.secure.fileupload.Jsons;
import net.genin.christophe.secure.fileupload.Utils;
import net.genin.christophe.secure.fileupload.models.Event;
import net.genin.christophe.secure.fileupload.models.adapters.ConfigurationAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.CreateEventAdapter;

import java.util.List;

public class RegisterVerticle extends AbstractVerticle implements ConfigurationAdapter, CreateEventAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterVerticle.class);
    public static final String REGISTER_NEW = RegisterVerticle.class.getName() + ".register";

    @Override
    public void start() {
        vertx.eventBus().<JsonObject>consumer(REGISTER_NEW, msg ->
                Single.just(msg)
                        .map(Message::body)
                        .map(b -> b.mapTo(Event.class))
                        .flatMap(event -> event.register(this, this))
                        .subscribe(msg::reply, t -> {
                            LOG.error("REGISTER_NEW", t);
                            msg.fail(500, "error");
                        }));

        LOG.info("Launch registering verticle");
    }

    @Override
    public List<String> extensionsIdApplication(String idApplication) {
        return Jsons.to_String(config().getJsonArray(idApplication.toUpperCase() + "_EXTENSIONS"));
    }

    @Override
    public Long validateTimeByIdApplication(String idApplication) {
        return config().getLong(idApplication.toUpperCase() + "_EXPIRE");
    }

    @Override
    public Single<Boolean> createDuring(Event event, Long seconds) {
        final JsonObject value = JsonObject.mapFrom(event);
        final String key = Utils.createKey(event);
        final JsonObject msg = new JsonObject().put("key", key).put("value", value).put("during", seconds);
        return vertx.eventBus().rxRequest(DbVerticle.SAVE, msg)
                .map(m -> true);
    }

}
