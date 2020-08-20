package net.genin.christophe.secure.fileupload.domain.adapters;

import net.genin.christophe.secure.fileupload.domain.entities.Upload;

public interface SaveUploadAdapter {

    void saveAndNotify(Upload upload);
}
