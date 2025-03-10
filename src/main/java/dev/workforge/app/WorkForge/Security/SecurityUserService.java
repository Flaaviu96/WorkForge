package dev.workforge.app.WorkForge.Security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SecurityUserService {
    private final RedisTemplate<String, SecurityUser> redisTemplate;


    public SecurityUserService(RedisTemplate<String, SecurityUser> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeUserInRedis(String sessionId, SecurityUser securityUser) {
        try {
            redisTemplate.opsForValue().set(sessionId, securityUser, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally log the error for easier troubleshooting
        }

    }

    public SecurityUser getUserFromRedis(String sessionId) {
        return redisTemplate.opsForValue().get(sessionId);
    }

    public void removeUserFromRedis(String sessionId) {
        redisTemplate.delete(sessionId);
    }
}
