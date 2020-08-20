package net.genin.christophe.secure.fileupload.domain.sanitizer;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.domain.entities.UploadedFile;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;

import java.util.Objects;
import java.util.Optional;

public class PdfSanitizer {
    private final UploadedFile uploadedFile;

    public PdfSanitizer(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Single<Boolean> sanitize(FileAdapter fileAdapter) {
        return fileAdapter.readContentFile(uploadedFile.getUploadedFileName())
                .map(PdfReader::new)
                .map(reader -> {
                    // Check 1:
                    // Detect if the document contains any JavaScript code
                    String jsCode = reader.getJavaScript();
                    if (Objects.isNull(jsCode))
                        throw new IllegalStateException("The file " + uploadedFile + " has embedded javascript");
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
                        throw new IllegalStateException("The file " + uploadedFile + " has embedded files");
                    }
                    return true;
                });
    }
}
