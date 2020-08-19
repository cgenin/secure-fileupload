package net.genin.christophe.secure.fileupload.models.adapters;

import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.models.entities.Event;

public interface UploadEventAdapter {
    Single<Event> findByIdApplication(String idApplication);

    void delete(Event event);
}
