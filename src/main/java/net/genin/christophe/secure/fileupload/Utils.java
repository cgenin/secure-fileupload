package net.genin.christophe.secure.fileupload;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;
import net.genin.christophe.secure.fileupload.domain.entities.Event;

public final class Utils {



    public static ConfigRetriever getConfigRetriever(Vertx vertx) {
        final ConfigStoreOptions optionsEnv = new ConfigStoreOptions().setType("env");
        final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions().addStore(optionsEnv);
        return ConfigRetriever.create(vertx, configRetrieverOptions);
    }


    public static String createKey(Event event) {
        return event.getState() + ":" + event.getId();
    }
}
