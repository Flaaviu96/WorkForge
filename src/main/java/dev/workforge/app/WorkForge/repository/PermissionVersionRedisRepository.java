package dev.workforge.app.WorkForge.repository;


public interface PermissionVersionRedisRepository {

    long incrementVersion(String key);

    Long getVersion(String key);

    void deleteVersion(String key);

    boolean exists(String key);
}
