package net.genin.christophe.secure.fileupload.domain.entities;

import java.util.Map;

public class UploadResponse {
    private int status;
    private Map<String, Integer> filesStatus;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, Integer> getFilesStatus() {
        return filesStatus;
    }

    public void setFilesStatus(Map<String, Integer> filesStatus) {
        this.filesStatus = filesStatus;
    }
}
