package net.genin.christophe.secure.fileupload.domain;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import net.genin.christophe.secure.fileupload.domain.adapters.ConfigurationAdapter;
import net.genin.christophe.secure.fileupload.domain.adapters.CreateEventAdapter;
import net.genin.christophe.secure.fileupload.domain.entities.Event;
import net.genin.christophe.secure.fileupload.domain.entities.EventState;

import java.util.*;

/**
 * Use Case register an query of uploading
 */
public class QueryUpload {

    public final Event event;

    public QueryUpload(Event event) {
        this.event = event;
    }


    public Single<JsonObject> save(ConfigurationAdapter configurationAdapter, CreateEventAdapter createEventAdapter) {
        final String idApplication = event.getIdApplication();
        Objects.requireNonNull(idApplication, "idApplication must not be null");
        event.setCreated(new Date().getTime());
        event.setId(UUID.randomUUID().toString());
        event.setState(EventState.registered.name());
        final Optional<List<String>> extensions = Optional.ofNullable(configurationAdapter.extensionsIdApplication(idApplication))
                .filter(l -> !l.isEmpty());
        if (!extensions.isPresent()) {
            return Single.error(new IllegalArgumentException("No extensions found for " + idApplication));
        }

        event.setExtensions(extensions.get());

        return Optional.ofNullable(configurationAdapter.validateTimeByIdApplication(idApplication))
                .map(s -> createEventAdapter.createDuring(event, s)
                        .map(b -> new JsonObject().put("id", event.getId()))
                )
                .orElseGet(() -> Single.error(new IllegalArgumentException("No validate time found for " + idApplication)));
    }

}
