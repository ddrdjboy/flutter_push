package com.isamanthena.push.controller;

import com.isamanthena.push.dto.ApiResponse;
import com.isamanthena.push.dto.DeviceRegisterRequest;
import com.isamanthena.push.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody DeviceRegisterRequest req) {
        deviceService.register(req.getUserId(), req.getToken(), req.getPlatform());
        return ResponseEntity.ok(new ApiResponse(true, "Device registered"));
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<ApiResponse> unregister(@PathVariable String token) {
        boolean result = deviceService.unregister(token);
        if (result) {
            return ResponseEntity.ok(new ApiResponse(true, "Device unregistered"));
        }
        return ResponseEntity.notFound().build();
    }
}
