package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Security.UserSessionService;
import dev.workforge.app.WorkForge.Service.AccessControlService;
import dev.workforge.app.WorkForge.Service.SecurityUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;

@Service
public class AccessControlServiceImpl implements AccessControlService {

    private final SecurityUserService securityUserService;
    private final UserSessionService userSessionService;

    public AccessControlServiceImpl(SecurityUserService securityUserService, UserSessionService userSessionService) {
        this.securityUserService = securityUserService;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean hasPermissions(Long projectId, PermissionType[] permissionTypes, String sessionId) {
        if (projectId == null) {
            return false;
        }

        SecurityUser securityUser = retrieveSecurityUser();

        if (sessionId != null && hasPermissionsChanged(sessionId)) {
            securityUserService.refreshUserPermissionsForUserDetails(securityUser);
            userSessionService.storeUserInRedis(sessionId, securityUser);
        }

        Map<Long, Set<Permission>> permissions = securityUser.getPermissionMap();

        if (permissions.containsKey(projectId) && hasRequiredPermissions(permissionTypes, permissions, projectId)) {
            return true;
        }

        throw new AccessDeniedException("User does not have permission to access project " + projectId);
    }

    private boolean hasRequiredPermissions(PermissionType[] permissionTypes, Map<Long, Set<Permission>> permissions, long projectId) {
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

    @Override
    public int[] getAvailableProjectsForCurrentUser() {
        SecurityUser securityUser = retrieveSecurityUser();
        Map<Long, Set<Permission>> permissions = securityUser.getPermissionMap();
        return permissions.keySet().stream()
                .mapToInt(Long::intValue)
                .toArray();
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

    private SecurityUser retrieveSecurityUser() {
        return (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
