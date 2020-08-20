package net.genin.christophe.secure.fileupload.domain.service;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.domain.entities.File;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;

import java.util.Objects;
import java.util.Optional;

class PdfSanitizer {
    private final File file;

    public PdfSanitizer(File file) {
        this.file = file;
    }

    public Single<Boolean> sanitize(FileAdapter fileAdapter) {
        return fileAdapter.readContentFile(file.getUploadedFileName())
                .map(PdfReader::new)
                .map(reader -> {
                    // Check 1:
                    // Detect if the document contains any JavaScript code
                    String jsCode = reader.getJavaScript();
                    if (Objects.nonNull(jsCode))
                        throw new IllegalStateException("The file " + file + " has embedded javascript");
                    return reader;
                })
                .map(reader -> {
                    // OK no JS code then when pass to check 2:
                    // Detect if the document has any embedded files
                    PdfArray namesArray = Optional.of(reader.getCatalog())
                            .map(r -> r.getAsDict(PdfName.NAMES))
                            .map(n -> n.getAsDict(PdfName.EMBEDDEDFILES))
                            .map(e -> e.getAsArray(PdfName.NAMES))
                            .orElse(null);
                    if (Objects.nonNull(namesArray) && !namesArray.isEmpty()) {
                        throw new IllegalStateException("The file " + file + " has embedded files");
                    }
                    return true;
                });
    }
}
