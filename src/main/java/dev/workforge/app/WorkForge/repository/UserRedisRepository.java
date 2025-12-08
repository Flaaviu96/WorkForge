package dev.workforge.app.WorkForge.repository;
import dev.workforge.app.WorkForge.security.UserPrincipal;

public interface UserRedisRepository {

    void save(String key, UserPrincipal value);

    void delete(String key);

    boolean hasKey(String key);

    UserPrincipal find(String key);
}

