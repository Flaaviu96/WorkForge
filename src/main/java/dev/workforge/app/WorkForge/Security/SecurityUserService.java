package dev.workforge.app.WorkForge.Security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SecurityUserService {
    private final RedisTemplate<String, Object> redisTemplate;


    public SecurityUserService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeUserInRedis(String sessionId, SecurityUser securityUser) {
        redisTemplate.opsForValue().set(sessionId, securityUser, 30, TimeUnit.MINUTES);
    }

    public SecurityUser getUserFromRedis(String sessionId) {
        return (SecurityUser) redisTemplate.opsForValue().get(sessionId);
    }

    public void removeUserFromRedis(String sessionId) {
        redisTemplate.delete(sessionId);
    }
}
