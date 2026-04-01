package com.isamanthena.push.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.isamanthena.push.dto.PushRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApnsService {

    private final ApnsClient apnsClient;

    @Value("${apns.bundle-id}")
    private String bundleId;

    public boolean send(String token, PushRequest req) {
        String payload = "{\"aps\":{\"alert\":{\"title\":\"" + req.getTitle()
                + "\",\"body\":\"" + req.getBody() + "\"},\"sound\":\"default\"}}";

        SimpleApnsPushNotification notification =
                new SimpleApnsPushNotification(token, bundleId, payload);

        try {
            PushNotificationFuture<SimpleApnsPushNotification,
                    PushNotificationResponse<SimpleApnsPushNotification>> future =
                    apnsClient.sendNotification(notification);

            PushNotificationResponse<SimpleApnsPushNotification> response =
                    future.get();

            if (response.isAccepted()) {
                return true;
            } else {
                log.error("APNs rejected token {}: {}", token,
                        response.getRejectionReason().orElse("unknown"));
                return false;
            }
        } catch (Exception e) {
            log.error("APNs send failed for token {}: {}", token, e.getMessage());
            return false;
        }
    }
}
