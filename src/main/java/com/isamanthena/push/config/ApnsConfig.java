package com.isamanthena.push.config;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
//

@Configuration
public class ApnsConfig {

    @Value("${apns.key-file}")
    private Resource keyFile;

    @Value("${apns.key-id}")
    private String keyId;

    @Value("${apns.team-id}")
    private String teamId;

    @Value("${apns.production}")
    private boolean production;

    @Bean
    public ApnsClient apnsClient() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String host = production
                ? ApnsClientBuilder.PRODUCTION_APNS_HOST
                : ApnsClientBuilder.DEVELOPMENT_APNS_HOST;

        return new ApnsClientBuilder()
                .setApnsServer(host)
                .setSigningKey(ApnsSigningKey.loadFromInputStream(
                        keyFile.getInputStream(), teamId, keyId))
                .build();
    }
}
