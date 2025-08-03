package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Projections.UserPermissionProjection;
import dev.workforge.app.WorkForge.Security.PermissionContext;
import dev.workforge.app.WorkForge.Security.PermissionContextOperation;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Service.SecurityUserService;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecurityUserServiceImpl implements SecurityUserService {

    private final UserPermissionService userPermissionService;

    public SecurityUserServiceImpl(UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    @Override
    public void loadUserPermissionsIntoUserDetails(UserDetails userDetails) {
        List<UserPermissionProjection> userPermission = userPermissionService.getPermissionsForUser(userDetails.getUsername());
        addPermissionsToUser(userDetails, userPermission, false);
    }

    @Override
    public void refreshUserPermissionsForUserDetails(UserDetails userDetails) {
        List<UserPermissionProjection> userPermission = userPermissionService.getPermissionsForUser(userDetails.getUsername());
        addPermissionsToUser(userDetails, userPermission, true);
    }

    @Override
    public List<PermissionType> getProjectPermissionForUser(long projectId) {
        Map<Long, Set<Permission>> permissions = getPermissionContext().getPermissionMap();
        if (!permissions.isEmpty()) {
            return permissions.get(projectId).stream()
                    .map(Permission::getPermissionType)
                    .toList();
        }
        return null;
    }

    private void addPermissionsToUser(UserDetails userDetails, List<UserPermissionProjection> userPermissionList, boolean updatePermissions) {
        if (userPermissionList.isEmpty()) {
            return;
        }
        if (updatePermissions) {
            getPermissionContextOperation().clearMap();
        }

        for (UserPermissionProjection userPermission : userPermissionList) {
            getPermissionContextOperation().addPermissions(userPermission.getProjectId(), userPermission.getPermissions());
        }
    }

    public PermissionContext getPermissionContext() {
        return ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPermissionContext();
    }

    public PermissionContextOperation getPermissionContextOperation() {
        Object principal = getPermissionContext();
        if (principal instanceof PermissionContextOperation) {
            return (PermissionContextOperation) principal;
        }
        throw new IllegalStateException("Principal does not implement PermissionContextOperation");
    }

    public PermissionContext getPermissionContext(SecurityUser user) {
        return user != null ? user.getPermissionContext() : null;
    }

    public PermissionContextOperation getPermissionContextOperation(SecurityUser user) {
        if (user.getPermissionContext() instanceof PermissionContextOperation) {
            return (PermissionContextOperation) user.getPermissionContext();
        }
        throw new IllegalArgumentException("User does not implement PermissionContextOperation");
    }


    @Override
    public SecurityUser retrieveSecurityUser() {
        return ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
