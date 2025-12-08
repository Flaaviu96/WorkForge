package dev.workforge.app.WorkForge.repository.impl;


import dev.workforge.app.WorkForge.repository.PermissionVersionRedisRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionVersionRedisRepositoryImpl implements PermissionVersionRedisRepository {

    private final RedisTemplate<Object, Object> redisTemplate;

        public PermissionVersionRedisRepositoryImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public long incrementVersion(String key) {
        Long newVersion = redisTemplate.opsForValue().increment(key);
        return newVersion == null ? 0 : newVersion;
    }

    @Override
    public Long getVersion(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Number num) {
            return num.longValue();
        }
        return null;
    }

    @Override
    public void deleteVersion(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
