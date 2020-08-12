package net.genin.christophe.secure.fileupload.models;

import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.models.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.models.sanitizer.ImageSanitizer;
import net.genin.christophe.secure.fileupload.models.sanitizer.PdfSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadedFile {
    private static final Logger LOG = LoggerFactory.getLogger(UploadedFile.class);

    public static final Pattern EXTENSIONS = Pattern.compile("\\.(.*)$");
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

    public Single<UploadState> valid(Event event, FileAdapter fileAdapter) {

        if (fileName.split("\\.").length != 2) {
            return Single.just(UploadState.multiple_extensions);
        }
        final Matcher matcher = EXTENSIONS.matcher(fileName);
        if (!matcher.find()) {
            return Single.just(UploadState.invalid_extension);
        }

        final String extensionStr = matcher.group(1);
        final Extensions extension = Extensions.parseByExtension(extensionStr);
        if (Objects.isNull(extension)) {
            return Single.just(UploadState.invalid_extension);
        }
        final boolean isAuthorize = event.getExtensions().stream()
                .map(Extensions::valueOf)
                .anyMatch(extension::equals);
        if (!isAuthorize) {
            return Single.just(UploadState.invalid_extension);
        }

        switch (extension) {
            case PNG:
            case JPEG:
                return new ImageSanitizer(this)
                        .sanitize(extension, fileAdapter)
                        .map(b -> UploadState.valid)
                        .onErrorResumeNext(t -> {
                            LOG.error("Error in sanitizing file " + this, t);
                            return Single.just(UploadState.wrong_sanitization);
                        });
            case PDF:
                return new PdfSanitizer(this)
                        .sanitize(fileAdapter)
                        .map(b-> UploadState.valid)
                        .onErrorResumeNext(t -> {
                            LOG.error("Error in sanitizing file " + this, t);
                            return Single.just(UploadState.wrong_sanitization);
                        });
            default:
                LOG.warn("not sanitize type " + extension);
        }


        return Single.just(UploadState.valid);
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
