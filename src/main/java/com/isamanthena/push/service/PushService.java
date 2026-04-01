package com.isamanthena.push.service;

import com.isamanthena.push.dto.PushRequest;
import com.isamanthena.push.model.Device;
import com.isamanthena.push.model.Platform;
import com.isamanthena.push.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    private final DeviceRepository deviceRepository;
    private final FcmService fcmService;
    private final ApnsService apnsService;

    public boolean sendToDevice(String token, PushRequest req) {
        Optional<Device> opt = deviceRepository.findByToken(token);
        if (opt.isEmpty() || !opt.get().isActive()) {
            log.warn("Device not found or inactive: {}", token);
            return false;
        }
        return dispatch(opt.get(), req);
    }

    public int broadcast(PushRequest req) {
        List<Device> devices = deviceRepository.findAllByActiveTrue();
        int success = 0;
        for (Device device : devices) {
            if (dispatch(device, req)) success++;
        }
        return success;
    }

    private boolean dispatch(Device device, PushRequest req) {
        if (device.getPlatform() == Platform.ANDROID) {
            return fcmService.send(device.getToken(), req);
        } else {
            return apnsService.send(device.getToken(), req);
        }
    }
}