package com.isamanthena.push.service;

import com.google.firebase.messaging.*;
import com.isamanthena.push.dto.PushRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    public boolean send(String token, PushRequest req) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(req.getTitle())
                        .setBody(req.getBody())
                        .build())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("FCM send failed for token {}: {}", token, e.getMessage());
            return false;
        }
    }
}
