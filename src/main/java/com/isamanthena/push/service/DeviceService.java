package com.isamanthena.push.service;

import com.isamanthena.push.model.Device;
import com.isamanthena.push.model.Platform;
import com.isamanthena.push.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public Device register(String userId, String token, Platform platform) {
        Device device = deviceRepository.findByUserId(userId)
                .orElse(new Device());
        device.setUserId(userId);
        device.setToken(token);
        device.setPlatform(platform);
        device.setActive(true);
        return deviceRepository.save(device);
    }

    public boolean unregister(String token) {
        Optional<Device> opt = deviceRepository.findByToken(token);
        if (opt.isEmpty()) return false;
        Device device = opt.get();
        device.setActive(false);
        deviceRepository.save(device);
        return true;
    }

    public Optional<Device> findByToken(String token) {
        return deviceRepository.findByToken(token);
    }
}
