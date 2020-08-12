package net.genin.christophe.secure.fileupload.models;

import java.util.Arrays;

public enum Extensions {
    JPEG("JPEG", "JPG"),
    PNG("PNG"),
    PDF("PDF");

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

    public String getMimetype() {
        switch (this){
            case JPEG:
                return "image/jpeg";
            case PNG:
                return "image/png";
            case PDF:
                return "application/pdf";
            default:
                return "";
        }
    }
}
