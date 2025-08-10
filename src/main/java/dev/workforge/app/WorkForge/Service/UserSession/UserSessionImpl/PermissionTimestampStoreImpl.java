package dev.workforge.app.WorkForge.Service.UserSession.UserSessionImpl;

import dev.workforge.app.WorkForge.Model.UserPermissionSec;
import dev.workforge.app.WorkForge.Repository.UserRedisRepository;
import dev.workforge.app.WorkForge.Service.UserSession.PermissionTimestampStore;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PermissionTimestampStoreImpl implements PermissionTimestampStore {
    private final UserRedisRepository redisRepository;
    private static final String PERMISSION_UPDATED_PREFIX = "PERMISSION_LAST_UPDATED:";

    public PermissionTimestampStoreImpl(UserRedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public void save(String userId, UserPermissionSec userPermissionSec) {
        this.redisRepository.set(PERMISSION_UPDATED_PREFIX + userId, userPermissionSec, 30, TimeUnit.MINUTES) ;
    }

    @Override
    public UserPermissionSec find(String userId) {
        String key = PERMISSION_UPDATED_PREFIX + userId;
        return redisRepository.get(key, UserPermissionSec.class);
    }

    @Override
    public void updateTimeStamp(String userId) {
        UserPermissionSec userPermissionSec = find(userId);
        userPermissionSec.setUpdatedPermission(System.currentTimeMillis());
        this.save(userId, userPermissionSec);
    }

    @Override
    public boolean hasKey(String userId) {
        return redisRepository.exists(userId);
    }
}
