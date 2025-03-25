package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Service.SecurityUserService;
import dev.workforge.app.WorkForge.Service.ServiceImpl.SecurityUserServiceImpl;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.Map;
import java.util.Set;

@Aspect
@Component
public class PermissionCheckAspect {

    private final UserSessionService userSessionService;
    private final SecurityUserService securityUserService;

    public PermissionCheckAspect(UserSessionService userSessionService, UserPermissionService userPermissionService, SecurityUserService securityUserService) {
        this.userSessionService = userSessionService;
        this.securityUserService = securityUserService;
    }

    @Before("@annotation(permissionCheck) && args(projectId)")
    public void checkPermission(PermissionCheck permissionCheck, Long projectId) {
        String sessionId = getCurrentHttpRequest().getRequestedSessionId();
        if (sessionId!= null && hasPermissionsChanged(sessionId)) {
            securityUserService.refreshUserPermissionsForUserDetails(retrieveSecurityUser());
            userSessionService.storeUserInRedis(sessionId, retrieveSecurityUser());
        }

        Map<Long, Set<Permission>> permissions = retrieveSecurityUser().getPermissionMap();
        if (permissions.containsKey(projectId) && hasPermissions(permissionCheck.permissionType(), permissions, projectId)) {
            return;
        }
        throw new AccessDeniedException("User does not have permission to access the project"+ projectId);
    }

    private SecurityUser retrieveSecurityUser() {
        return (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean hasPermissions(PermissionType[] permissionTypes, Map<Long, Set<Permission>> permissions, long projectId) {
        Set<Permission> permissionSet = permissions.get(projectId);
        if (permissionSet == null) {
            return false;
        }

        if (hasWriteWithoutRead(permissionSet)) {
            return false;
        }
        for (PermissionType permissionType : permissionTypes) {
            if (permissionSet.stream().noneMatch(permission -> permission.getPermissionType() == permissionType)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasWriteWithoutRead(Set<Permission> permissions) {
        boolean hasWrite = permissions.stream().anyMatch(permission -> permission.getPermissionType() == PermissionType.WRITE);
        boolean hasRead = permissions.stream().anyMatch(permission -> permission.getPermissionType() == PermissionType.READ);

        return hasWrite && !hasRead;
    }
    private boolean hasPermissionsChanged(String sessionId) {
        long lastPermissionsUpdateFromRedis = userSessionService.getPermissionFromRedis(String.valueOf(retrieveSecurityUser().getId()));
        long lastPermissionsUpdateFromContext = retrieveSecurityUser().getLastPermissionsUpdate();
        return lastPermissionsUpdateFromRedis > lastPermissionsUpdateFromContext;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
