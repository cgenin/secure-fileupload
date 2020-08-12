package net.genin.christophe.secure.fileupload.models.adapters;

import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.models.Event;

public interface CreateEventAdapter {

    Single<Boolean> createDuring(Event event, Long seconds);
}
