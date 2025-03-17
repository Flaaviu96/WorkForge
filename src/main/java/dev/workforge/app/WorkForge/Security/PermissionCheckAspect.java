package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
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

    private final SecurityUserService securityUserService;
    private final UserPermissionService userPermissionService;

    public PermissionCheckAspect(SecurityUserService securityUserService, UserPermissionService userPermissionService) {
        this.securityUserService = securityUserService;
        this.userPermissionService = userPermissionService;
    }

    @Before("@annotation(permissionCheck) && args(projectId)")
    public void checkPermission(PermissionCheck permissionCheck, Long projectId) {
        String sessionId = getCurrentHttpRequest().getRequestedSessionId();
        if (sessionId!= null && hasPermissionsChanged(sessionId)) {
            userPermissionService.refreshUserPermissions(retrieveSecurityUser());
            securityUserService.storeUserInRedis(sessionId, retrieveSecurityUser());
        }
        Map<Long, Set<Permission>> permissions = retrieveSecurityUser().getPermissionMap();
        if (permissions.containsKey(projectId) && permissions.get(projectId).contains(new Permission().setPermissionType(permissionCheck.permissionType()))) {
            return;
        }
        throw new AccessDeniedException("User does not have permission to access the project"+ projectId);
    }

    private SecurityUser retrieveSecurityUser() {
        return (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean hasPermissionsChanged(String sessionId) {
        return false;
//        String checksumFromSecurityContext = retrieveSecurityUser().computeChecksum();
//        String checksumFromRedis = securityUserService.getChecksumFromRedis(sessionId);
//        return checksumFromRedis != null && checksumFromSecurityContext != null && !checksumFromSecurityContext.equals(checksumFromRedis);
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
