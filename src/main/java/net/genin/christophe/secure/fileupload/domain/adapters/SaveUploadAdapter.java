package net.genin.christophe.secure.fileupload.domain.adapters;

import net.genin.christophe.secure.fileupload.domain.entities.UploadEvent;

public interface SaveUploadAdapter {

    void saveAndNotify(UploadEvent uploadEvent);
}
