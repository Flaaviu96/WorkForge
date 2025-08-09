package dev.workforge.app.WorkForge.Repository.Impl;

import dev.workforge.app.WorkForge.Repository.UserRedisRepository;
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
    public <T> void set(String key, T value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    @Override
    public void delete(String sessionId) {
        redisTemplate.delete(sessionId);
    }

    @Override
    public boolean exists(String sessionId) {
        return false;
    }

    @Override
    public <T> T get(String sessionId, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(sessionId);
        if (value == null) return null;
        if (!type.isInstance(value)) {
            throw new IllegalStateException("Expected type " + type.getSimpleName());
        }
        return type.cast(value);
    }
}
