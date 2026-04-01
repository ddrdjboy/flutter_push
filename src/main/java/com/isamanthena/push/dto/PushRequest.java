package com.isamanthena.push.dto;

import lombok.Data;

@Data
public class PushRequest {
    private String title;
    private String body;
}