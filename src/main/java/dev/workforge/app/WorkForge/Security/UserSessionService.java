package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Model.UserPermissionSec;
import dev.workforge.app.WorkForge.Security.SecurityUser.PermissionContext;
import dev.workforge.app.WorkForge.Security.SecurityUser.SecurityUser;
import dev.workforge.app.WorkForge.Service.UserSession.PermissionTimestampStore;
import dev.workforge.app.WorkForge.Service.Other.SecurityUserService;
import dev.workforge.app.WorkForge.Service.UserSession.UserSessionStore;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {
    private final SecurityUserService securityUserService;
    private final PermissionTimestampStore permissionTimestampStore;
    private final UserSessionStore userSessionStore;

    public UserSessionService(@Lazy SecurityUserService securityUserService, RedisTemplate<Object, Object> redisTemplate, PermissionTimestampStore permissionTimestampStore, UserSessionStore userSessionStore) {
        this.securityUserService = securityUserService;
        this.permissionTimestampStore = permissionTimestampStore;
        this.userSessionStore = userSessionStore;
    }

    public void storeUserOnLogin(String sessionId, SecurityUser securityUser) {
        if (sessionId != null && securityUser != null) {
            userSessionStore.save(sessionId, securityUser);
            UserPermissionSec userPermission = new UserPermissionSec();
            permissionTimestampStore.save(String.valueOf(securityUser.getId()), userPermission);
        }
    }

    public void storeUserInRedis(String sessionId, SecurityUser securityUser) {
        if (sessionId != null && securityUser != null) {
            userSessionStore.save(sessionId, securityUser);
            PermissionContext permissionContext = securityUser.getPermissionContext();
            if (permissionContext != null
                    && permissionContext.getBuildPermissionAt() != 0
                    && permissionContext.getUpdatedPermission() != 0) {

                UserPermissionSec userPermission = new UserPermissionSec(
                        permissionContext.getBuildPermissionAt(),
                        permissionContext.getUpdatedPermission()
                );
                permissionTimestampStore.save(String.valueOf(securityUser.getId()),userPermission);
            }
        }
    }

    public UserPermissionSec getPermission(String userId) {
        return permissionTimestampStore.find(userId);
    }

    public SecurityUser getUserFromRedis(String sessionId) {
        SecurityUser securityUser = userSessionStore.find(sessionId);
        if (securityUser == null) {
            return null;
        }
        UserPermissionSec userPermission = permissionTimestampStore.find(String.valueOf(securityUser.getId()));
        if (userPermission == null) {
            return securityUser;
        }
        securityUserService.getPermissionContext(securityUser).setBuildPermissionAt(userPermission.getBuildPermissionAt());
        securityUserService.getPermissionContext(securityUser).setUpdatedPermission(userPermission.getUpdatedPermission());
        return securityUser;
    }

    public boolean hasKey(String sessionId) {
        return userSessionStore.hasKey(sessionId);
    }
}