package net.genin.christophe.secure.fileupload.domain.adapters;

import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.domain.entities.Event;

public interface CreateEventAdapter {

    Single<Boolean> createDuring(Event event, Long seconds);
}
