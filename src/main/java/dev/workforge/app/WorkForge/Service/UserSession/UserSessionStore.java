package dev.workforge.app.WorkForge.Service.UserSession;

import dev.workforge.app.WorkForge.Security.SecurityUser.SecurityUser;

public interface UserSessionStore {
    void save(String sessionId, SecurityUser securityUser);
    SecurityUser find(String sessionId);
    void delete(String sessionId);
    boolean hasKey(String sessionId);
}
