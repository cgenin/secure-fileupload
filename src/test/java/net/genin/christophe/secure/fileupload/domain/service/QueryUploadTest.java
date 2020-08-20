package net.genin.christophe.secure.fileupload.domain.service;


import io.reactivex.Single;
import net.genin.christophe.secure.fileupload.domain.adapters.ConfigurationAdapter;
import net.genin.christophe.secure.fileupload.domain.adapters.CreateEventAdapter;
import net.genin.christophe.secure.fileupload.domain.entities.Event;
import net.genin.christophe.secure.fileupload.domain.service.QueryUpload;
import net.genin.christophe.secure.fileupload.domain.valueobject.EventState;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryUploadTest {

    private Event event;
    private CreateEventAdapter createEventAdapter;
    private ConfigurationAdapter configurationAdapter;

    @Before
    public void init() {
        event = new Event();
        event.setIdApplication("TEST");
        event.setIdClient("001");
        createEventAdapter = mock(CreateEventAdapter.class);
        configurationAdapter = mock(ConfigurationAdapter.class);
    }

    @Test
    public void should_register() {
        when(configurationAdapter.extensionsIdApplication("TEST")).thenReturn(Collections.singletonList("PDF"));
        when(configurationAdapter.validateTimeByIdApplication("TEST")).thenReturn(60L);
        when(createEventAdapter.createDuring(event, 60L)).thenReturn(Single.just(true));
        new QueryUpload(event).save(configurationAdapter, createEventAdapter)
                .subscribe(json -> {
                    assertThat(json.getString("id")).isNotEmpty().isNotBlank();
                    assertThat(event.getCreated()).isNotNull();
                    assertThat(event.getExtensions()).isNotNull();
                    assertThat(event.getState()).isEqualTo(EventState.registered.name());
                    assertThat(event.getId()).isEqualTo(json.getString("id"));
                });
    }

    @Test
    public void should_not_register_Notknown() {
        when(configurationAdapter.extensionsIdApplication("TEST2")).thenReturn(Collections.singletonList("PDF"));
        when(configurationAdapter.validateTimeByIdApplication("TEST2")).thenReturn(60L);
        when(createEventAdapter.createDuring(event, 60L)).thenReturn(Single.just(true));
        new QueryUpload(event).save(configurationAdapter, createEventAdapter)
                .subscribe(json -> {
                            assertThat(json).isNull();
                        },
                        t -> {
                            assertThat(t).isNotNull();
                        });
    }

    @Test
    public void should_not_register_with_no_time() {
        when(configurationAdapter.extensionsIdApplication("TEST")).thenReturn(Collections.singletonList("PDF"));
        when(configurationAdapter.validateTimeByIdApplication("TEST")).thenReturn(null);
        when(createEventAdapter.createDuring(event, 60L)).thenReturn(Single.just(true));
        new QueryUpload(event).save(configurationAdapter, createEventAdapter)
                .subscribe(json -> {
                            assertThat(json).isNull();
                        },
                        t -> {
                            assertThat(t).isNotNull();
                        });
    }
}
