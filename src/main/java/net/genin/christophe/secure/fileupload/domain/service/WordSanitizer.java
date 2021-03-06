package net.genin.christophe.secure.fileupload.domain.service;

import com.aspose.words.*;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.domain.entities.File;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

class WordSanitizer {
    private final File file;

    /**
     * List of allowed Word format (WML = Word ML (Word 2003 XML)).<br>
     * Allow also DOCM because it can exists without macro inside.<br>
     * Allow also DOT/DOTM because both can exists without macro inside.<br>
     * We reject MHTML file because:<br>
     * <ul>
     * <li>API cannot detect macro into this format</li>
     * <li>Is not normal to use this format to represent a Word file (there plenty of others supported format)</li>
     * </ul>
     */
    private static final List<String> ALLOWED_FORMAT = Arrays.asList("doc", "docx", "docm", "wml", "dot", "dotm");

    private static final Pattern REPLACE_DOTS = Pattern.compile("\\.");

    public WordSanitizer(File file) {
        this.file = file;
    }

    private boolean isExtensionAuthorize(String ext) {
        String fe = REPLACE_DOTS.matcher(ext.toLowerCase(Locale.US)).replaceAll("");
        return ALLOWED_FORMAT.contains(fe);
    }

    public Single<Boolean> sanitize(FileAdapter fileAdapter) {
        return fileAdapter.readContentFile(file.getUploadedFileName())
                .map(testFileFormat())
                .map(testContent())
                .subscribeOn(Schedulers.computation());
    }

    @SuppressWarnings("unchecked")
    public Function<byte[], Boolean> testContent() {
        return b -> {
            final Document document = new Document(new ByteArrayInputStream(b));
            if (document.hasMacros()) {
                throw new IllegalStateException("the document has Macro :" + file);
            }
            final NodeCollection<Shape> childNodes = document.getChildNodes(NodeType.SHAPE, true);
            for (int i = 0; i < childNodes.getCount(); i++) {
                Shape shape = (Shape) childNodes.get(i);
                if (Objects.nonNull(shape.getOleFormat())) {
                    throw new IllegalStateException("The document has OLE Object " + file);
                }
            }

            return true;
        };
    }

    public Function<byte[], byte[]> testFileFormat() {
        return b -> {
            FileFormatInfo formatInfo = FileFormatUtil.detectFileFormat(new ByteArrayInputStream(b));
            String formatExtension = FileFormatUtil.loadFormatToExtension(formatInfo.getLoadFormat());
            if (!isExtensionAuthorize(formatExtension)) {
                throw new IllegalStateException("Extension not authorized " + formatExtension + "/" + file);
            }

            return b;
        };
    }

}
