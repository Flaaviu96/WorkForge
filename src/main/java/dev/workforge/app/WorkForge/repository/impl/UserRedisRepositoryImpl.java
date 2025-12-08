package dev.workforge.app.WorkForge.repository.impl;

import dev.workforge.app.WorkForge.repository.UserRedisRepository;
import dev.workforge.app.WorkForge.security.UserPrincipal;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UserRedisRepositoryImpl implements UserRedisRepository {

    private final RedisTemplate<Object, Object> redisTemplate;

    public UserRedisRepositoryImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String key, UserPrincipal value) {
        redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public UserPrincipal find(String key) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }
        return (UserPrincipal) value;
    }
}
