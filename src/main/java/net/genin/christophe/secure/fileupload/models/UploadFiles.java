package net.genin.christophe.secure.fileupload.models;

import io.reactivex.Observable;
import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.models.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.SaveUploadAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.UploadEventAdapter;
import net.genin.christophe.secure.fileupload.models.entities.Upload;
import net.genin.christophe.secure.fileupload.models.entities.UploadResponse;
import net.genin.christophe.secure.fileupload.models.entities.UploadState;
import net.genin.christophe.secure.fileupload.models.entities.UploadedFile;

import java.util.HashMap;

public class UploadFiles {

    public final Upload upload;

    public UploadFiles(Upload upload) {
        this.upload = upload;
    }

    public Single<UploadResponse> upload(UploadEventAdapter uploadEventAdapter, FileAdapter fileAdapter,
                                         SaveUploadAdapter saveUploadAdapter) {
        final String idApplication = upload.getIdApplication();
        return uploadEventAdapter.findByIdApplication(idApplication)
                .flatMap(event -> {
                    upload.setEvent(event);
                    // Suppress Event for not reusing
                    uploadEventAdapter.delete(event);
                    return Observable.fromIterable(upload.getFiles())
                            .flatMap(uf ->
                                    new ValidOneFile(uf)
                                            .valid(event, fileAdapter)
                                            .map(state -> {
                                                if (!state.equals(UploadState.valid)) {
                                                    fileAdapter.delete(uf.getUploadedFileName());
                                                    uf.setErrorCode(state.name());
                                                }


                                                final HashMap<String, Integer> map = new HashMap<>();
                                                map.put(uf.getFileName(), state.getCode());
                                                return map;
                                            })
                                            .toObservable()
                            )
                            .reduce(new HashMap<String, Integer>(), (acc, v) -> {
                                acc.putAll(v);
                                return acc;
                            })
                            .doOnSuccess(m -> saveUploadAdapter.saveAndNotify(upload))
                            .map(map -> {
                                final UploadResponse uploadResponse = new UploadResponse();
                                final Integer code = map.values().stream().max(Integer::compareTo).orElse(200);
                                uploadResponse.setStatus(code);
                                uploadResponse.setFilesStatus(map);
                                return uploadResponse;
                            });
                })
                // Suppress all files if no event found
                .doOnError(t ->
                        upload.getFiles().stream().map(UploadedFile::getUploadedFileName)
                                .forEach(fileAdapter::delete)
                )
                // Message for no event found
                .onErrorReturn(t -> {
                    final UploadResponse uploadResponse = new UploadResponse();
                    uploadResponse.setStatus(UploadState.id_application_not_found.getCode());
                    return uploadResponse;
                });
    }
}
