package com.isamanthena.push.service;

import com.isamanthena.push.dto.PushRequest;
import com.isamanthena.push.model.Device;
import com.isamanthena.push.model.Platform;
import com.isamanthena.push.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PushServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private FcmService fcmService;

    @Mock
    private ApnsService apnsService;

    @InjectMocks
    private PushService pushService;

    private PushRequest buildRequest() {
        PushRequest req = new PushRequest();
        req.setTitle("Hello");
        req.setBody("World");
        return req;
    }

    private Device buildDevice(String token, Platform platform, boolean active) {
        Device d = new Device();
        d.setToken(token);
        d.setPlatform(platform);
        d.setActive(active);
        return d;
    }

    @Test
    void sendToDevice_deviceNotFound_returnsFalse() {
        when(deviceRepository.findByToken("t1")).thenReturn(Optional.empty());

        boolean result = pushService.sendToDevice("t1", buildRequest());

        assertThat(result).isFalse();
        verifyNoInteractions(fcmService, apnsService);
    }

    @Test
    void sendToDevice_inactiveDevice_returnsFalse() {
        when(deviceRepository.findByToken("t1"))
                .thenReturn(Optional.of(buildDevice("t1", Platform.ANDROID, false)));

        boolean result = pushService.sendToDevice("t1", buildRequest());

        assertThat(result).isFalse();
        verifyNoInteractions(fcmService, apnsService);
    }

    @Test
    void sendToDevice_androidDevice_routesToFcm() {
        PushRequest req = buildRequest();
        when(deviceRepository.findByToken("t1"))
                .thenReturn(Optional.of(buildDevice("t1", Platform.ANDROID, true)));
        when(fcmService.send("t1", req)).thenReturn(true);

        boolean result = pushService.sendToDevice("t1", req);

        assertThat(result).isTrue();
        verify(fcmService).send("t1", req);
        verifyNoInteractions(apnsService);
    }

    @Test
    void sendToDevice_iosDevice_routesToApns() {
        PushRequest req = buildRequest();
        when(deviceRepository.findByToken("t1"))
                .thenReturn(Optional.of(buildDevice("t1", Platform.IOS, true)));
        when(apnsService.send("t1", req)).thenReturn(true);

        boolean result = pushService.sendToDevice("t1", req);

        assertThat(result).isTrue();
        verify(apnsService).send("t1", req);
        verifyNoInteractions(fcmService);
    }

    @Test
    void sendToDevice_fcmFails_returnsFalse() {
        PushRequest req = buildRequest();
        when(deviceRepository.findByToken("t1"))
                .thenReturn(Optional.of(buildDevice("t1", Platform.ANDROID, true)));
        when(fcmService.send("t1", req)).thenReturn(false);

        boolean result = pushService.sendToDevice("t1", req);

        assertThat(result).isFalse();
    }

    @Test
    void broadcast_countsOnlySuccessfulSends() {
        PushRequest req = buildRequest();
        Device android = buildDevice("t1", Platform.ANDROID, true);
        Device ios = buildDevice("t2", Platform.IOS, true);
        when(deviceRepository.findAllByActiveTrue()).thenReturn(List.of(android, ios));
        when(fcmService.send("t1", req)).thenReturn(true);
        when(apnsService.send("t2", req)).thenReturn(false);

        int count = pushService.broadcast(req);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void broadcast_allSucceed_returnsFullCount() {
        PushRequest req = buildRequest();
        Device d1 = buildDevice("t1", Platform.ANDROID, true);
        Device d2 = buildDevice("t2", Platform.ANDROID, true);
        when(deviceRepository.findAllByActiveTrue()).thenReturn(List.of(d1, d2));
        when(fcmService.send(anyString(), eq(req))).thenReturn(true);

        int count = pushService.broadcast(req);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void broadcast_noActiveDevices_returnsZero() {
        when(deviceRepository.findAllByActiveTrue()).thenReturn(List.of());

        int count = pushService.broadcast(buildRequest());

        assertThat(count).isEqualTo(0);
        verifyNoInteractions(fcmService, apnsService);
    }
}
