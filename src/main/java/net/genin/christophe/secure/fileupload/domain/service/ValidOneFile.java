package net.genin.christophe.secure.fileupload.domain.service;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.domain.entities.Event;
import net.genin.christophe.secure.fileupload.domain.valueobject.Extensions;
import net.genin.christophe.secure.fileupload.domain.valueobject.UploadState;
import net.genin.christophe.secure.fileupload.domain.entities.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class ValidOneFile {
    private static final Logger LOG = LoggerFactory.getLogger(File.class);

    public static final Pattern EXTENSIONS = Pattern.compile("\\.(.*)$");
    public final File file;

    public ValidOneFile(File file) {
        this.file = file;
    }


    public Single<UploadState> valid(Event event, FileAdapter fileAdapter) {
        final String fileName = file.getFileName();
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

        final Function<Throwable, SingleSource<UploadState>> errorResumeNext = t -> {
            LOG.error("Error in sanitizing file " + this, t);
            return Single.just(UploadState.wrong_sanitization);
        };
        switch (extension) {
            case PNG:
            case JPEG:
                return new ImageSanitizer(file)
                        .sanitize(extension, fileAdapter)
                        .map(b -> UploadState.valid)
                        .onErrorResumeNext(errorResumeNext);
            case PDF:
                return new PdfSanitizer(file)
                        .sanitize(fileAdapter)
                        .map(b -> UploadState.valid)
                        .onErrorResumeNext(errorResumeNext);
            case WORD:
                return new WordSanitizer(file)
                        .sanitize(fileAdapter)
                        .map(b -> UploadState.valid)
                        .onErrorResumeNext(errorResumeNext);
            case EXCEL:
                return new ExcelsSanitizer(file)
                        .sanitize(fileAdapter)
                        .map(b -> UploadState.valid)
                        .onErrorResumeNext(errorResumeNext);
            default:
                LOG.warn("not sanitize type " + extension);
        }


        return Single.just(UploadState.valid);
    }
}
