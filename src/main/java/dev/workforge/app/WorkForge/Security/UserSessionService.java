package dev.workforge.app.WorkForge.Security;

import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserSessionService {
    private final RedisTemplate<Object, Object> redisTemplate;
    private static final String USER_PREFIX = "USER_SESSION:";
    private static final String PERMISSION_UPDATED_PREFIX = "PERMISSION_LAST_UPDATED:";



    public UserSessionService(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeUserInRedis(String key, SecurityUser securityUser) {
        try {
            if (key != null && securityUser != null) {
                redisTemplate.opsForValue().set(USER_PREFIX + key, securityUser, 30, TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(PERMISSION_UPDATED_PREFIX + securityUser.getId(), System.currentTimeMillis(), 30, TimeUnit.MINUTES);
            }
        } catch (Exception ts) {
            ts.printStackTrace();
        }
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(USER_PREFIX + key);
    }

    public SecurityUser getUserFromRedis(String key) {
        return getObjectFromRedis(USER_PREFIX + key, SecurityUser.class);
    }

    public Long getChecksumFromRedis(String key) {
        return getObjectFromRedis(PERMISSION_UPDATED_PREFIX + key, Long.class);
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
