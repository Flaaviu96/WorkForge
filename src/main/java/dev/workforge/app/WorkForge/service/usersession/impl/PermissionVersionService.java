package dev.workforge.app.WorkForge.service.usersession.impl;

import dev.workforge.app.WorkForge.repository.PermissionVersionRedisRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionVersionService {

    private static final String VERSION_PREFIX = "PERMISSION_VERSION:";

    private final PermissionVersionRedisRepository versionRepository;

    public PermissionVersionService(PermissionVersionRedisRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    public long increment(String username) {
        return versionRepository.incrementVersion(VERSION_PREFIX + username);
    }

    public Long get(String username) {
        return versionRepository.getVersion(VERSION_PREFIX + username);
    }

    public void reset(String username) {
        versionRepository.deleteVersion(VERSION_PREFIX + username);
    }

    public boolean exists(String username) {
        return versionRepository.exists(VERSION_PREFIX + username);
    }
}
