package com.isamanthena.push.controller;

import com.isamanthena.push.dto.ApiResponse;
import com.isamanthena.push.dto.PushRequest;
import com.isamanthena.push.service.PushService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PushControllerTest {

    @Mock
    private PushService pushService;

    @InjectMocks
    private PushController pushController;

    private PushRequest buildRequest() {
        PushRequest req = new PushRequest();
        req.setTitle("Hello");
        req.setBody("World");
        return req;
    }

    @Test
    void sendToDevice_success_returnsOkWithSuccessTrue() {
        PushRequest req = buildRequest();
        when(pushService.sendToDevice(eq("token1"), any())).thenReturn(true);

        ResponseEntity<ApiResponse> response = pushController.sendToDevice("token1", req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Push sent");
    }

    @Test
    void sendToDevice_deviceNotFound_returnsOkWithSuccessFalse() {
        PushRequest req = buildRequest();
        when(pushService.sendToDevice(eq("token1"), any())).thenReturn(false);

        ResponseEntity<ApiResponse> response = pushController.sendToDevice("token1", req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Push failed or device not found");
    }

    @Test
    void broadcast_returnsSuccessWithCountInMessage() {
        when(pushService.broadcast(any())).thenReturn(5);

        ResponseEntity<ApiResponse> response = pushController.broadcast(buildRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Sent to 5 devices");
    }

    @Test
    void broadcast_noDevices_returnsZeroInMessage() {
        when(pushService.broadcast(any())).thenReturn(0);

        ResponseEntity<ApiResponse> response = pushController.broadcast(buildRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Sent to 0 devices");
    }
}
