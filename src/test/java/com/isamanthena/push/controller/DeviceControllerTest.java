package com.isamanthena.push.controller;

import com.isamanthena.push.dto.ApiResponse;
import com.isamanthena.push.dto.DeviceRegisterRequest;
import com.isamanthena.push.model.Device;
import com.isamanthena.push.model.Platform;
import com.isamanthena.push.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    private DeviceRegisterRequest buildRequest(String userId, String token, Platform platform) {
        DeviceRegisterRequest req = new DeviceRegisterRequest();
        req.setUserId(userId);
        req.setToken(token);
        req.setPlatform(platform);
        return req;
    }

    @Test
    void register_androidDevice_returns200WithSuccess() {
        when(deviceService.register("user1", "token1", Platform.ANDROID)).thenReturn(new Device());

        ResponseEntity<ApiResponse> response = deviceController.register(
                buildRequest("user1", "token1", Platform.ANDROID));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Device registered");
        verify(deviceService).register("user1", "token1", Platform.ANDROID);
    }

    @Test
    void register_iosDevice_returns200WithSuccess() {
        when(deviceService.register("user2", "iosToken", Platform.IOS)).thenReturn(new Device());

        ResponseEntity<ApiResponse> response = deviceController.register(
                buildRequest("user2", "iosToken", Platform.IOS));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    void unregister_existingToken_returns200WithSuccess() {
        when(deviceService.unregister("token1")).thenReturn(true);

        ResponseEntity<ApiResponse> response = deviceController.unregister("token1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Device unregistered");
    }

    @Test
    void unregister_nonExistentToken_returns404() {
        when(deviceService.unregister("unknown")).thenReturn(false);

        ResponseEntity<ApiResponse> response = deviceController.unregister("unknown");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
