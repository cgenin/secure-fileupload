package net.genin.christophe.secure.fileupload.domain.service;

import io.reactivex.Observable;
import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.domain.adapters.SaveUploadAdapter;
import net.genin.christophe.secure.fileupload.domain.adapters.UploadEventAdapter;
import net.genin.christophe.secure.fileupload.domain.entities.UploadEvent;
import net.genin.christophe.secure.fileupload.domain.valueobject.UploadResponse;
import net.genin.christophe.secure.fileupload.domain.valueobject.UploadState;
import net.genin.christophe.secure.fileupload.domain.entities.File;

import java.util.HashMap;

public class UploadFiles {

    public final UploadEvent uploadEvent;

    public UploadFiles(UploadEvent uploadEvent) {
        this.uploadEvent = uploadEvent;
    }

    public Single<UploadResponse> run(UploadEventAdapter uploadEventAdapter, FileAdapter fileAdapter,
                                      SaveUploadAdapter saveUploadAdapter) {
        final String idApplication = uploadEvent.getIdApplication();
        return uploadEventAdapter.findByIdApplication(idApplication)
                .flatMap(event -> {
                    uploadEvent.setEvent(event);
                    // Suppress Event for not reusing
                    uploadEventAdapter.delete(event);
                    return Observable.fromIterable(uploadEvent.getFiles())
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
                            .doOnSuccess(m -> saveUploadAdapter.saveAndNotify(uploadEvent))
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
                        uploadEvent.getFiles().stream().map(File::getUploadedFileName)
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
