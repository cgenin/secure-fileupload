package net.genin.christophe.secure.fileupload.domain.entities;

import java.util.Objects;

@SuppressWarnings("unused")
public class UploadedFile {

    private String name;
    private String uploadedFileName;
    private String fileName;
    private String contentType;
    private String contentTransferEncoding;
    private String charSet;
    private long size;
    private String errorCode;

    public UploadedFile() {
    }

    public UploadedFile(String name, String uploadedFileName, String fileName, String contentType, String contentTransferEncoding, String charSet, long size) {
        this.name = name;
        this.uploadedFileName = uploadedFileName;
        this.fileName = fileName;
        this.contentType = contentType;
        this.contentTransferEncoding = contentTransferEncoding;
        this.charSet = charSet;
        this.size = size;
    }


    public String getName() {
        return name;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    public String getCharSet() {
        return charSet;
    }

    public long getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadedFile that = (UploadedFile) o;
        return size == that.size &&
                Objects.equals(name, that.name) &&
                Objects.equals(uploadedFileName, that.uploadedFileName) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(contentTransferEncoding, that.contentTransferEncoding) &&
                Objects.equals(charSet, that.charSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uploadedFileName, fileName, contentType, contentTransferEncoding, charSet, size);
    }

    @Override
    public String toString() {
        return "UploadedFile{" +
                "name='" + name + '\'' +
                ", uploadedFileName='" + uploadedFileName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentTransferEncoding='" + contentTransferEncoding + '\'' +
                ", charSet='" + charSet + '\'' +
                ", size=" + size +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
