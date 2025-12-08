package dev.workforge.app.WorkForge.service.impl;

import dev.workforge.app.WorkForge.exceptions.PermissionException;
import dev.workforge.app.WorkForge.model.Permission;
import dev.workforge.app.WorkForge.model.PermissionType;
import dev.workforge.app.WorkForge.projections.UserPermissionProjection;
import dev.workforge.app.WorkForge.security.UserPrincipal;
import dev.workforge.app.WorkForge.service.other.AccessControlService;
import dev.workforge.app.WorkForge.security.SecurityUserService;
import dev.workforge.app.WorkForge.service.user_permission.UserPermissionService;
import dev.workforge.app.WorkForge.service.usersession.impl.PermissionVersionService;
import dev.workforge.app.WorkForge.service.usersession.impl.UserPermissionCacheServiceImpl;
import dev.workforge.app.WorkForge.util.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AccessControlServiceImpl implements AccessControlService {

    private final SecurityUserService securityUserService;
    private final UserPermissionCacheServiceImpl userPermissionCacheService;
    private final UserPermissionService userPermissionService;
    private final PermissionVersionService permissionVersionService;

    public AccessControlServiceImpl(SecurityUserService securityUserService, UserPermissionCacheServiceImpl userPermissionCacheRepository, UserPermissionService userPermissionService, PermissionVersionService permissionVersionService) {
        this.securityUserService = securityUserService;
        this.userPermissionCacheService = userPermissionCacheRepository;
        this.userPermissionService = userPermissionService;
        this.permissionVersionService = permissionVersionService;
    }

    @Override
    public boolean hasPermissions(Long projectId, List<PermissionType> permissionTypes, String sessionId) {
        if (projectId == null) {
            return false;
        }

        UserPrincipal userPrincipal = securityUserService.getContext().retrieveSecurityUser();
        final long redisVersion = permissionVersionService.get(sessionId);
        if (sessionId != null && hasPermissionsChanged(sessionId, redisVersion)) {
            List<UserPermissionProjection> userPermissionProjections = userPermissionService.getPermissionsForUser(userPrincipal.getUsername());
            securityUserService.getPermissionManager().reloadPermissionsIntoPermissionContext(userPrincipal, userPermissionProjections);
            securityUserService.getMaintenance().setVersion(redisVersion);
            userPermissionCacheService.storeUserPermissionInRedis(sessionId, userPrincipal);
        }

        Map<Long, Set<Permission>> permissions = userPrincipal.getPermissionContext().getPermissionMap();

        if (permissions.containsKey(projectId) && hasRequiredPermissions(permissionTypes, permissions, projectId)) {
            return true;
        }

        throw new PermissionException(ErrorMessages.PROJECT_VIEW_PERMISSION_DENIED, HttpStatus.FORBIDDEN);
    }

    /**
     * Checks if the user has the required permissions to access the specified project.
     *
     * @param permissionTypes the required permission types (e.g., READ, WRITE, ADMIN) the user must have
     * @param permissions a map of project IDs to the sets of permissions assigned to the user
     * @param projectId the ID of the project the user is trying to access
     * @return true if the user has all required permissions for the project and does not have WRITE without READ; false otherwise
     */
    private boolean hasRequiredPermissions(List<PermissionType> permissionTypes, Map<Long, Set<Permission>> permissions, long projectId) {
        Set<Permission> permissionSet = permissions.get(projectId);
        if (permissionSet == null) {
            return false;
        }

        if (hasWriteWithoutRead(permissionSet)) {
            return false;
        }
        for (PermissionType permissionType : permissionTypes) {
            boolean result = permissionSet.stream().noneMatch(permission -> permission.getPermissionType() == permissionType);
            if (result) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int[] getAvailableProjectsForCurrentUser() {

        Map<Long, Set<Permission>> permissions = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPermissionContext().getPermissionMap();
        return permissions.keySet().stream()
                .mapToInt(Long::intValue)
                .toArray();
    }

    /**
     * Checks if the user has WRITE permission but does not have READ permission.
     *
     * @param permissions the set of permissions assigned to the user
     * @return true if the user has WRITE permission and does not have READ permission; false otherwise
     */
    private boolean hasWriteWithoutRead(Set<Permission> permissions) {
        boolean hasWrite = permissions.stream().anyMatch(permission -> permission.getPermissionType() == PermissionType.WRITE);
        boolean hasRead = permissions.stream().anyMatch(permission -> permission.getPermissionType() == PermissionType.READ);

        return hasWrite && !hasRead;
    }

    /**
     * Checks if any permissions for the current user have changed.
     *
     * @param sessionId the session ID of the user
     * @return true if the user's permissions have changed; false otherwise
     */
    private boolean hasPermissionsChanged(String sessionId, long redisVersion) {
        UserPrincipal securityUser = securityUserService.getContext().retrieveSecurityUser();
        long localVersion = securityUser.getPermissionContext().getVersion();
        return redisVersion > localVersion;
    }
}
