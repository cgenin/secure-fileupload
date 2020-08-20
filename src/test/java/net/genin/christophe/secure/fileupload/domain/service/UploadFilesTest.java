package net.genin.christophe.secure.fileupload.domain.service;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import net.genin.christophe.secure.fileupload.domain.adapters.FileAdapter;
import net.genin.christophe.secure.fileupload.domain.adapters.SaveUploadAdapter;
import net.genin.christophe.secure.fileupload.domain.adapters.UploadEventAdapter;
import net.genin.christophe.secure.fileupload.domain.entities.Event;
import net.genin.christophe.secure.fileupload.domain.entities.UploadEvent;
import net.genin.christophe.secure.fileupload.domain.entities.File;
import net.genin.christophe.secure.fileupload.domain.valueobject.UploadResponse;
import net.genin.christophe.secure.fileupload.domain.valueobject.UploadState;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UploadFilesTest {
    private Event event;
    private FileAdapter fileAdapter;
    private byte[] jpegBytes;
    private TestObserver<UploadResponse> testSubscriber;
    private byte[] pngBytes;
    private UploadEvent uploadEvent;
    private UploadEventAdapter uploadEventAdapter;
    private SaveUploadAdapter saveUploadAdapter;


    @Before
    public void init() throws IOException {
        File file = new File();
        file.setFileName("test.jpeg");
        file.setUploadedFileName("toto");
        uploadEvent = new UploadEvent();
        uploadEvent.setIdApplication("TEST");
        uploadEvent.setFiles(Collections.singletonList(file));
        event = new Event();
        event.setIdApplication("TEST");
        event.setExtensions(Collections.singletonList("JPEG"));
        fileAdapter = mock(FileAdapter.class);
        testSubscriber = new TestObserver<>();

        jpegBytes = Files.readAllBytes(Paths.get("src/test/resources/Moon.jpg"));
        pngBytes = Files.readAllBytes(Paths.get("src/test/resources/android.png"));
        when(fileAdapter.write(anyString(), any(byte[].class))).thenReturn(Single.just(true));
        when(fileAdapter.readContentFile("toto")).thenReturn(Single.just(jpegBytes));
        uploadEventAdapter = mock(UploadEventAdapter.class);

    }

    @Test
    public void should_be_ok() throws InterruptedException {
        when(uploadEventAdapter.findByIdApplication("TEST"))                .thenReturn(Single.just(event));


        saveUploadAdapter = mock(SaveUploadAdapter.class);
        new UploadFiles(uploadEvent).run(uploadEventAdapter, fileAdapter, saveUploadAdapter)
                .subscribe(testSubscriber);
        testSubscriber.await();

        testSubscriber.assertOf(t->{
            final UploadResponse uploadResponse = t.values().get(0);
            assertThat(uploadResponse.getStatus()).isEqualTo(UploadState.valid.getCode());
            assertThat(uploadResponse.getFilesStatus()).isNotEmpty()
                .allSatisfy((name, code)->{
                    assertThat(name).isEqualTo("test.jpeg");
                    assertThat(code).isEqualTo(200);
                });

        });
    }

    @Test
    public void should_be_ko_if_Application_not_found() throws InterruptedException {
        when(uploadEventAdapter.findByIdApplication("TEST"))
                .thenReturn(Single.error(new IllegalStateException()));

        saveUploadAdapter = mock(SaveUploadAdapter.class);
        new UploadFiles(uploadEvent).run(uploadEventAdapter, fileAdapter, saveUploadAdapter)
                .subscribe(testSubscriber);
        testSubscriber.await();

        testSubscriber.assertOf(t->{
            final UploadResponse uploadResponse = t.values().get(0);
            assertThat(uploadResponse.getStatus()).isEqualTo(UploadState.id_application_not_found.getCode());
            assertThat(uploadResponse.getFilesStatus()).isNullOrEmpty();

        });
    }
}
