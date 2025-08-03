package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Service.SecurityUserService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Service
public class UserSessionService {
    private final SecurityUserService securityUserService;
    private final RedisTemplate<Object, Object> redisTemplate;
    private static final String USER_PREFIX = "USER_SESSION:";
    private static final String PERMISSION_UPDATED_PREFIX = "PERMISSION_LAST_UPDATED:";

    public UserSessionService(@Lazy SecurityUserService securityUserService, RedisTemplate<Object, Object> redisTemplate) {
        this.securityUserService = securityUserService;
        this.redisTemplate = redisTemplate;
    }

    public void storeUserOnLogin(String sessionId, SecurityUser securityUser) {
        if (sessionId != null && securityUser != null) {
            redisTemplate.opsForValue().set(USER_PREFIX + sessionId, securityUser, 30, TimeUnit.MINUTES);

            UserPermission userPermission = new UserPermission();
            redisTemplate.opsForValue().set(
                    PERMISSION_UPDATED_PREFIX + securityUser.getId(),
                    userPermission,
                    30, TimeUnit.MINUTES
            );
        }
    }

    public void storeUserInRedis(String sessionId, SecurityUser securityUser) {
        if (sessionId != null && securityUser != null) {
            redisTemplate.opsForValue().set(USER_PREFIX + sessionId, securityUser, 30, TimeUnit.MINUTES);

            PermissionContext permissionContext = securityUser.getPermissionContext();
            if (permissionContext != null
                    && permissionContext.getBuildPermissionAt() != 0
                    && permissionContext.getUpdatedPermission() != 0) {

                UserPermission userPermission = new UserPermission(
                        permissionContext.getBuildPermissionAt(),
                        permissionContext.getUpdatedPermission()
                );

                redisTemplate.opsForValue().set(
                        PERMISSION_UPDATED_PREFIX + securityUser.getId(),
                        userPermission,
                        30, TimeUnit.MINUTES
                );
            }
        }
    }

    @Setter
    @Getter
    public static class UserPermission implements Serializable {
        private long buildPermissionAt;
        private long updatedPermission;

        UserPermission(long buildPermissionAt, long updatedPermission) {
            this.buildPermissionAt = buildPermissionAt;
            this.updatedPermission = updatedPermission;
        }

        UserPermission() {
            this.buildPermissionAt = System.currentTimeMillis();
        }
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(USER_PREFIX + key);
    }

    public SecurityUser getUserFromRedis(String key) {
        SecurityUser securityUser = getObjectFromRedis(USER_PREFIX + key, SecurityUser.class);
        if (securityUser == null) {
            return null;
        }
        UserPermission userPermission = getPermissionTimestampsFromRedis(String.valueOf(securityUser.getId()));
        if (userPermission == null) {
            return securityUser;
        }
        securityUserService.getPermissionContext(securityUser).setBuildPermissionAt(userPermission.getBuildPermissionAt());
        securityUserService.getPermissionContext(securityUser).setUpdatedPermission(userPermission.getUpdatedPermission());
        return securityUser;
    }

    public UserPermission getPermissionTimestampsFromRedis(String key) {
        return getObjectFromRedis(PERMISSION_UPDATED_PREFIX + key, UserPermission.class);
    }

    public void updatePermissionTimestampsFromRedis(String key) {
        UserPermission userPermission = getObjectFromRedis(PERMISSION_UPDATED_PREFIX + key, UserPermission.class);
        if (userPermission != null) {
            userPermission.setUpdatedPermission(System.currentTimeMillis());
            redisTemplate.opsForValue().set(PERMISSION_UPDATED_PREFIX + key, userPermission, 30, TimeUnit.MINUTES);
        }
    }

    private <T> T getObjectFromRedis(@NonNull String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalStateException("Retrieved object is not of type " + type.getSimpleName());
        }
        return type.cast(value);
    }

    public void removeUserFromRedis(String key) {
        if (key != null) {
            redisTemplate.delete(key);
        }
    }
}