package net.genin.christophe.secure.fileupload.models;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import net.genin.christophe.secure.fileupload.models.adapters.ConfigurationAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.CreateEventAdapter;
import net.genin.christophe.secure.fileupload.models.entities.Event;
import net.genin.christophe.secure.fileupload.models.entities.EventState;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
        event.setExtensions(Optional.ofNullable(configurationAdapter.extensionsIdApplication(idApplication))
                .filter(l -> !l.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("No extensions found for " + idApplication)));
        final Long seconds = Optional.ofNullable(configurationAdapter.validateTimeByIdApplication(idApplication))
                .orElseThrow(() -> new IllegalArgumentException("No validate time found for " + idApplication));
        event.setCreated(new Date().getTime());
        event.setId(UUID.randomUUID().toString());
        event.setState(EventState.registered.name());
        return createEventAdapter.createDuring(event, seconds)
                .map(b -> new JsonObject().put("id", event.getId()));
    }

}
