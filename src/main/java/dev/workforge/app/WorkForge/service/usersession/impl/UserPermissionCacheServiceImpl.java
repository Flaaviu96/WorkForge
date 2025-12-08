package dev.workforge.app.WorkForge.service.usersession.impl;

import dev.workforge.app.WorkForge.repository.UserRedisRepository;
import dev.workforge.app.WorkForge.security.UserPrincipal;
import dev.workforge.app.WorkForge.service.usersession.UserPermissionCacheService;
import org.springframework.stereotype.Service;


@Service
public class UserPermissionCacheServiceImpl implements UserPermissionCacheService {
    private final UserRedisRepository userPermissionCacheRepository;

    public UserPermissionCacheServiceImpl(UserRedisRepository userPermissionCacheRepository) {
        this.userPermissionCacheRepository = userPermissionCacheRepository;
    }

    @Override
    public UserPrincipal getCachedPermissions(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        if (!userPermissionCacheRepository.hasKey(username)) {
            return null;
        }
        return userPermissionCacheRepository.find(username);
    }

    public void storeUserPermissionInRedis(String username, UserPrincipal userPrincipal) {
        if (username != null
                && userPrincipal != null
                && userPrincipal.getPermissionContext() != null
        ) {
            userPermissionCacheRepository.save(username, userPrincipal);
        }
    }
    public boolean hasKey(String username) {
        return userPermissionCacheRepository.hasKey(username);
    }


}
