package com.isamanthena.push.controller;

import com.isamanthena.push.dto.ApiResponse;
import com.isamanthena.push.dto.PushRequest;
import com.isamanthena.push.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor

// 11
public class PushController {

    private final PushService pushService;

    @PostMapping("/device/{token}")
    public ResponseEntity<ApiResponse> sendToDevice(
            @PathVariable String token,
            @RequestBody PushRequest req) {
        boolean result = pushService.sendToDevice(token, req);
        if (result) {
            return ResponseEntity.ok(new ApiResponse(true, "Push sent"));
        }
        return ResponseEntity.ok(new ApiResponse(false, "Push failed or device not found"));
    }

    @PostMapping("/broadcast")
    public ResponseEntity<ApiResponse> broadcast(@RequestBody PushRequest req) {
        int count = pushService.broadcast(req);
        return ResponseEntity.ok(new ApiResponse(true, "Sent to " + count + " devices"));
    }
}
