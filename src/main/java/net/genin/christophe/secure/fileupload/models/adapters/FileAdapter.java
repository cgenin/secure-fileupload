package net.genin.christophe.secure.fileupload.models.adapters;

import io.reactivex.Single;

public interface FileAdapter {

    Single<byte[]> readContentFile(String path);

    void delete(String path);

    Single<Boolean> write(String uploadedFileName, byte[] toByteArray);
}
