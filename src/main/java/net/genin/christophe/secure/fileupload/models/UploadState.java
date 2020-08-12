package net.genin.christophe.secure.fileupload.models;

public enum UploadState {
    valid(200),
    id_application_not_found(400),
    multiple_extensions(400),
    wrong_sanitization(400),
    invalid_extension(400);

    private final int code;


    private UploadState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
