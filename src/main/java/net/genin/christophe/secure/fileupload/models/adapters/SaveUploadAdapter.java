package net.genin.christophe.secure.fileupload.models.adapters;

import net.genin.christophe.secure.fileupload.models.entities.Upload;

public interface SaveUploadAdapter {

    void saveAndNotify(Upload upload);
}
