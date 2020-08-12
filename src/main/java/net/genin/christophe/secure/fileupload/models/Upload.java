package net.genin.christophe.secure.fileupload.models;

import io.reactivex.Observable;
import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.models.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.SaveUploadAdapter;
import net.genin.christophe.secure.fileupload.models.adapters.UploadEventAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class Upload {

    private String idApplication;
    private List<UploadedFile> files;
    private long created;
    private Event event;

    public Upload() {
    }

    public Upload(String idApplication, List<UploadedFile> files) {
        this.idApplication = idApplication;
        this.files = files;
        this.created = new Date().getTime();
    }

    public Single<UploadResponse> upload(UploadEventAdapter uploadEventAdapter, FileAdapter fileAdapter,
                                         SaveUploadAdapter saveUploadAdapter) {
        return uploadEventAdapter.findByIdApplication(idApplication)
                .flatMap(event -> {
                    setEvent(event);
                    // Suppress Event for not reusing
                    uploadEventAdapter.delete(event);
                    return Observable.fromIterable(getFiles())
                            .flatMap(uf ->
                                    uf.valid(event, fileAdapter)
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
                            .doOnSuccess(m -> saveUploadAdapter.saveAndNotify(this))
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
                        files.stream().map(UploadedFile::getUploadedFileName)
                                .forEach(fileAdapter::delete)
                )
                // Message for no event found
                .onErrorReturn(t -> {
                    final UploadResponse uploadResponse = new UploadResponse();
                    uploadResponse.setStatus(UploadState.id_application_not_found.getCode());
                    return uploadResponse;
                });
    }

    public void setIdApplication(String idApplication) {
        this.idApplication = idApplication;
    }

    public void setFiles(List<UploadedFile> files) {
        this.files = files;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
    }

    public String getIdApplication() {
        return idApplication;
    }

    public List<UploadedFile> getFiles() {
        return files;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}
