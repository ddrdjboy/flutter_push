package com.isamanthena.push.dto;

import com.isamanthena.push.model.Platform;
import lombok.Data;

@Data
public class DeviceRegisterRequest {
    private String userId;
    private String token;
    private Platform platform;
}