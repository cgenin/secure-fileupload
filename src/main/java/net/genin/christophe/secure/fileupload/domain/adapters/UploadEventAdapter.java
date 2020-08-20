package net.genin.christophe.secure.fileupload.domain.adapters;

import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.domain.entities.Event;

public interface UploadEventAdapter {
    Single<Event> findByIdApplication(String idApplication);

    void delete(Event event);
}
