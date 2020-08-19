package net.genin.christophe.secure.fileupload.models.entities;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import net.genin.christophe.secure.fileupload.models.adapters.ConfigurationAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.CreateEventAdapter;

import java.util.*;

public class Event {
    private String idApplication;
    private String idClient;
    private String id;
    private String state;
    private List<String> extensions;
    private long created;

    public Event() {
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

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }
}
