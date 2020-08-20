package net.genin.christophe.secure.fileupload.domain.sanitizer;

import io.reactivex.Observable;
import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.domain.entities.Extensions;
import net.genin.christophe.secure.fileupload.domain.entities.UploadedFile;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;
import net.sf.jmimemagic.Magic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ImageSanitizer {
    private static final Logger LOG = LoggerFactory.getLogger(ImageSanitizer.class);
    private final UploadedFile uploadedFile;

    public ImageSanitizer(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Single<Boolean> sanitize(Extensions extensions, FileAdapter fileAdapter) {
        return fileAdapter.readContentFile(uploadedFile.getUploadedFileName())
                .flatMap(data -> Observable.concat(
                        mimetypes(extensions, data).toObservable(),
                        resize(data, fileAdapter).toObservable()
                ).reduce(true, (acc, v) -> true));
    }

    public Single<Boolean> resize(byte[] data, FileAdapter fileAdapter) {
        return Single.just(data)
                .map(d -> {
                    final BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(d));
                    if (Objects.isNull(originalImage)) {
                        throw new IllegalStateException("Cannot read image " + uploadedFile);
                    }
                    int originalWidth = originalImage.getWidth(null);
                    int originalHeight = originalImage.getHeight(null);
                    Image resizedImage = originalImage.getScaledInstance(originalWidth - 1, originalHeight - 1, Image.SCALE_SMOOTH);
                    // Resize the resized image by adding 1px on Width and Height - In fact set image to is initial size
                    Image initialSizedImage = resizedImage.getScaledInstance(originalWidth, originalHeight, Image.SCALE_SMOOTH);
                    // Save image by overwriting the provided source file content
                    BufferedImage sanitizedImage = new BufferedImage(initialSizedImage.getWidth(null), initialSizedImage.getHeight(null),
                            BufferedImage.TYPE_INT_RGB);
                    Graphics bg = sanitizedImage.getGraphics();
                    bg.drawImage(initialSizedImage, 0, 0, null);
                    bg.dispose();
                    // Save File
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(sanitizedImage, "JPG", byteArrayOutputStream);
                    return byteArrayOutputStream;
                })
                .flatMap(ba -> fileAdapter.write(uploadedFile.getUploadedFileName(), ba.toByteArray()));
    }

    public Single<Boolean> mimetypes(Extensions extensions, byte[] data) {
        return Single.just(data)
                .map(Magic::getMagicMatch)
                .map(magicMatch -> {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("description of " + magicMatch.print());
                    }
                    final boolean isGoodMimeType = extensions.getMimetype()
                            .stream()
                            .anyMatch(s -> magicMatch.getMimeType().equals(s));
                    if (!isGoodMimeType) {
                        throw new IllegalStateException("Wrong mime types " + magicMatch.getMimeType() + "/" + extensions.getMimetype() + " for " + uploadedFile);
                    }
                    return true;
                });
    }
}
