package com.isamanthena.push.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.isamanthena.push.model.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByUserId(String userId);
    Optional<Device> findByToken(String token);
    List<Device> findAllByActiveTrue();
}