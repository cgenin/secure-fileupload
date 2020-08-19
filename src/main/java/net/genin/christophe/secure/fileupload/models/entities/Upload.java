package net.genin.christophe.secure.fileupload.models.entities;

import io.reactivex.Observable;
import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.models.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.SaveUploadAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.UploadEventAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class Upload {

    private String idApplication;
    private List<UploadedFile> files;
    private long created;
    private Event event;

    public Upload() {
    }

    public Upload(String idApplication, List<UploadedFile> files) {
        this.idApplication = idApplication;
        this.files = files;
        this.created = new Date().getTime();
    }



    public void setIdApplication(String idApplication) {
        this.idApplication = idApplication;
    }

    public void setFiles(List<UploadedFile> files) {
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

    public List<UploadedFile> getFiles() {
        return files;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}
