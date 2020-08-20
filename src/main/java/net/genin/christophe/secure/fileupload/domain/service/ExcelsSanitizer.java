package net.genin.christophe.secure.fileupload.domain.service;

import com.aspose.cells.*;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import net.genin.christophe.secure.fileupload.domain.entities.File;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

class ExcelsSanitizer {
    private final File file;

    private static final List<String> ALLOWED_FORMAT = Arrays.asList("xls", "xlsx", "xlsm", "xlsb", "xlt", "xltm" );

    private static final Pattern REPLACE_DOTS = Pattern.compile("\\.");

    public ExcelsSanitizer(File file) {
        this.file = file;
    }

    private boolean isExtensionAuthorize(String ext) {
        String fe = REPLACE_DOTS.matcher(ext.toLowerCase(Locale.US)).replaceAll("");
        return ALLOWED_FORMAT.contains(fe);
    }

    public Single<Boolean> sanitize(FileAdapter fileAdapter) {
        return fileAdapter.readContentFile(file.getUploadedFileName())
                .map(testFileFormat())
                .map(b -> new Workbook(new ByteArrayInputStream(b)))
                .flatMap(testContent())
                .subscribeOn(Schedulers.computation());
    }

    @SuppressWarnings("unchecked")
    public Function<Workbook, SingleSource<? extends Boolean>> testContent() {
        return workbook -> {
            if (workbook.hasMacro()) {
                return Single.error(new IllegalStateException("the Excel has Macro :" + file));
            }
            final CollectionBase<Worksheet> worksheets = workbook.getWorksheets();
            return Observable.fromIterable(worksheets)
                    .flatMap(sheet -> {
                        final CollectionBase<OleObject> oleObjects = sheet.getOleObjects();
                        return Observable.fromIterable(oleObjects);
                    })
                    .filter(oleObject -> oleObject.getMsoDrawingType() == MsoDrawingType.OLE_OBJECT)
                    .count()
                    .flatMap(nbErrors -> {
                        if (nbErrors > 0) {
                            return Single.error(new IllegalStateException("Found OLE objects " + nbErrors +
                                    "in all workbook sheets " + file));
                        }
                        return Single.just(true);
                    });
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
