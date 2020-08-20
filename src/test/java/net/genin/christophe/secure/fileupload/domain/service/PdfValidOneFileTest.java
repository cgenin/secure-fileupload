package net.genin.christophe.secure.fileupload.domain.service;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.domain.entities.Event;
import net.genin.christophe.secure.fileupload.domain.entities.File;
import net.genin.christophe.secure.fileupload.domain.valueobject.UploadState;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PdfValidOneFileTest {


    private File file;
    private Event event;
    private FileAdapter fileAdapter;
    private byte[] pdfBytes;
    private TestObserver<UploadState> testSubscriber;
    private byte[] pngBytes;


    @Before
    public void init() throws IOException {
        file = new File();
        file.setFileName("test.pdf");
        file.setUploadedFileName("toto");
        event = new Event();
        event.setIdApplication("TEST");
        event.setExtensions(Arrays.asList("PDF", "JPEG"));
        fileAdapter = mock(FileAdapter.class);
        testSubscriber = new TestObserver<>();

        pdfBytes = Files.readAllBytes(Paths.get("src/test/resources/google.pdf"));
        pngBytes = Files.readAllBytes(Paths.get("src/test/resources/android.png"));

    }

    @Test
    public void should_be_ok() {
        when(fileAdapter.readContentFile("toto")).thenReturn(Single.just(pdfBytes));
        when(fileAdapter.write(anyString(), any(byte[].class))).thenReturn(Single.just(true));
        new ValidOneFile(file)
                .valid(event, fileAdapter)
                .subscribe(testSubscriber);
        testSubscriber.assertValue(UploadState.valid);

    }

    @Test
    public void should_be_ko_if_content_multiple_extension() {
        file.setFileName("tyty.exe.pdf");
        when(fileAdapter.readContentFile("toto")).thenReturn(Single.just(pdfBytes));
        when(fileAdapter.write(anyString(), any(byte[].class))).thenReturn(Single.just(true));
        new ValidOneFile(file)
                .valid(event, fileAdapter)
                .subscribe(testSubscriber);
        testSubscriber.assertValue(UploadState.multiple_extensions);

    }


    @Test
    public void should_be_ko_if_another_type_content() {
        when(fileAdapter.readContentFile("toto")).thenReturn(Single.just(pngBytes));
        when(fileAdapter.write(anyString(), any(byte[].class))).thenReturn(Single.just(true));
        new ValidOneFile(file)
                .valid(event, fileAdapter)
                .subscribe(testSubscriber);
        testSubscriber.assertValue(UploadState.wrong_sanitization);
    }

    @Test
    public void should_be_ko_if_no_content() {
        when(fileAdapter.readContentFile("toto")).thenReturn(Single.just(new byte[0]));
        when(fileAdapter.write(anyString(), any(byte[].class))).thenReturn(Single.just(true));
        new ValidOneFile(file)
                .valid(event, fileAdapter)
                .subscribe(testSubscriber);
        testSubscriber.assertValue(UploadState.wrong_sanitization);
    }

    @Test
    public void should_be_ko_if_fail_to_read() {
        when(fileAdapter.readContentFile("toto")).thenReturn(Single.error(new IllegalStateException()));
        when(fileAdapter.write(anyString(), any(byte[].class))).thenReturn(Single.just(true));
        new ValidOneFile(file)
                .valid(event, fileAdapter)
                .subscribe(testSubscriber);
        testSubscriber.assertValue(UploadState.wrong_sanitization);
    }

}
