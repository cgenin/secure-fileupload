package net.genin.christophe.secure.fileupload.models;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import net.genin.christophe.secure.fileupload.models.adapters.ConfigurationAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.CreateEventAdapter;

import java.util.*;

public class Event {
    private String idApplication;
    private String id;
    private String state;
    private List<String> extensions;
    private long created;

    public Event() {
    }

    public Single<JsonObject> register(ConfigurationAdapter configurationAdapter, CreateEventAdapter createEventAdapter) {
        Objects.requireNonNull(idApplication, "idApplication must not be null");
        extensions = Optional.ofNullable(configurationAdapter.extensionsIdApplication(idApplication))
                .filter(l -> !l.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("No extensions found for " + idApplication));
        final Long seconds = Optional.ofNullable(configurationAdapter.validateTimeByIdApplication(idApplication))
                .orElseThrow(() -> new IllegalArgumentException("No validate time found for " + idApplication));
        created = new Date().getTime();
        setId(UUID.randomUUID().toString());
        state = EventState.registered.name();
        return createEventAdapter.createDuring(this, seconds)
                .map(b -> new JsonObject().put("id", id));
    }


    public String getIdApplication() {
        return idApplication;
    }

    public void setIdApplication(String idApplication) {
        this.idApplication = idApplication;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
