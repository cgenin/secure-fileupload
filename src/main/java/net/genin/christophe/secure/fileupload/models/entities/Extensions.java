package net.genin.christophe.secure.fileupload.models.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Extensions {
    JPEG("JPEG", "JPG"),
    PNG("PNG"),
    PDF("PDF"),
    WORD("DOC", "DOCX"),
    EXCEL("XLS", "XLSX", "XLSM", "XLSB", "XLT", "XLTM"),
    ;

    private final String[] extensions;

    Extensions(String... extensions) {
        this.extensions = extensions;
    }

    public static Extensions parseByExtension(String extension) {
        for (Extensions ex : values()) {
            if (Arrays.stream(ex.extensions).anyMatch(e -> e.equalsIgnoreCase(extension))) {
                return ex;
            }
        }
        return null;
    }

    public List<String> getMimetype() {
        switch (this) {
            case JPEG:
                return Collections.singletonList("image/jpeg");
            case PNG:
                return Collections.singletonList("image/png");
            case PDF:
                return Collections.singletonList("application/pdf");
            case WORD:
                return Arrays.asList("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case EXCEL:
                return Arrays.asList("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default:
                return Collections.emptyList();
        }
    }
}
