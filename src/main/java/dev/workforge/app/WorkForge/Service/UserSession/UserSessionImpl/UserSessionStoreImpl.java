package dev.workforge.app.WorkForge.Service.UserSession.UserSessionImpl;

import dev.workforge.app.WorkForge.Repository.UserRedisRepository;
import dev.workforge.app.WorkForge.Security.SecurityUser.SecurityUser;
import dev.workforge.app.WorkForge.Service.UserSession.UserSessionStore;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserSessionStoreImpl implements UserSessionStore {
    private static final String USER_PREFIX = "USER_SESSION:";
    private final UserRedisRepository redisRepository;

    public UserSessionStoreImpl(UserRedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public void save(String sessionId, SecurityUser securityUser) {
        redisRepository.set(USER_PREFIX + sessionId, securityUser, 30, TimeUnit.MINUTES);
    }

    @Override
    public SecurityUser find(String sessionId) {
        return redisRepository.get(USER_PREFIX + sessionId, SecurityUser.class);
    }

    @Override
    public void delete(String sessionId) {
        redisRepository.delete(USER_PREFIX + sessionId);
    }

    @Override
    public boolean hasKey(String sessionId) {
        return redisRepository.exists(sessionId);
    }
}
