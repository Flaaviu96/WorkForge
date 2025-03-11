package dev.workforge.app.WorkForge.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.context.SecurityContextRepository;
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
            if (sessionId != null && securityUser != null) {
                redisTemplate.opsForValue().set(sessionId, securityUser, 30, TimeUnit.MINUTES);
            }
        } catch (Exception ignored) {
        }
    }

    public boolean hasKey(String sessionId) {
        return redisTemplate.hasKey(sessionId);
    }

    public SecurityUser getUserFromRedis(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return redisTemplate.opsForValue().get(sessionId);
    }

    public void removeUserFromRedis(String sessionId) {
        if (sessionId != null) {
            redisTemplate.delete(sessionId);
        }
    }
}
