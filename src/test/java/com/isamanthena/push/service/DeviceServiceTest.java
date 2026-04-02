package com.isamanthena.push.service;

import com.isamanthena.push.model.Device;
import com.isamanthena.push.model.Platform;
import com.isamanthena.push.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void register_newDevice_savesAndReturnsDevice() {
        when(deviceRepository.findByUserId("user1")).thenReturn(Optional.empty());
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        Device result = deviceService.register("user1", "token1", Platform.ANDROID);

        assertThat(result.getUserId()).isEqualTo("user1");
        assertThat(result.getToken()).isEqualTo("token1");
        assertThat(result.getPlatform()).isEqualTo(Platform.ANDROID);
        assertThat(result.isActive()).isTrue();
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void register_existingDevice_updatesTokenAndPlatform() {
        Device existing = new Device();
        existing.setUserId("user1");
        existing.setToken("oldToken");
        existing.setPlatform(Platform.IOS);
        when(deviceRepository.findByUserId("user1")).thenReturn(Optional.of(existing));
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        Device result = deviceService.register("user1", "newToken", Platform.ANDROID);

        assertThat(result.getToken()).isEqualTo("newToken");
        assertThat(result.getPlatform()).isEqualTo(Platform.ANDROID);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void unregister_existingToken_deactivatesDeviceAndReturnsTrue() {
        Device device = new Device();
        device.setToken("token1");
        device.setActive(true);
        when(deviceRepository.findByToken("token1")).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        boolean result = deviceService.unregister("token1");

        assertThat(result).isTrue();
        assertThat(device.isActive()).isFalse();
        verify(deviceRepository).save(device);
    }

    @Test
    void unregister_nonExistentToken_returnsFalse() {
        when(deviceRepository.findByToken("unknown")).thenReturn(Optional.empty());

        boolean result = deviceService.unregister("unknown");

        assertThat(result).isFalse();
        verify(deviceRepository, never()).save(any());
    }

    @Test
    void findByToken_delegatesToRepository() {
        Device device = new Device();
        device.setToken("token1");
        when(deviceRepository.findByToken("token1")).thenReturn(Optional.of(device));

        Optional<Device> result = deviceService.findByToken("token1");

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("token1");
    }

    @Test
    void findByToken_notFound_returnsEmpty() {
        when(deviceRepository.findByToken("missing")).thenReturn(Optional.empty());

        Optional<Device> result = deviceService.findByToken("missing");

        assertThat(result).isEmpty();
    }
}
