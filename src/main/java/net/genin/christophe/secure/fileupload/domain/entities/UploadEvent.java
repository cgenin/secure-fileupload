package net.genin.christophe.secure.fileupload.domain.entities;

import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class UploadEvent {

    private String idApplication;
    private List<File> files;
    private long created;
    private Event event;

    public UploadEvent() {
    }

    public UploadEvent(String idApplication, List<File> files) {
        this.idApplication = idApplication;
        this.files = files;
        this.created = new Date().getTime();
    }



    public void setIdApplication(String idApplication) {
        this.idApplication = idApplication;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
    }

    public String getIdApplication() {
        return idApplication;
    }

    public List<File> getFiles() {
        return files;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}
