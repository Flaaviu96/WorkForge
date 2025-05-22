package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Projections.UserPermissionProjection;
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
    public List<Long> extractProjectIdsFromSecurityContext() {
        Map<Long, Set<Permission>> permissions = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPermissionMap();
        return permissions.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(permission -> permission.getPermissionType() == PermissionType.READ))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public void refreshUserPermissionsForUserDetails(UserDetails userDetails) {
        List<UserPermissionProjection> userPermission = userPermissionService.getPermissionsForUser(userDetails.getUsername());
        addPermissionsToUser(userDetails, userPermission, true);
    }

    private void addPermissionsToUser(UserDetails userDetails, List<UserPermissionProjection> userPermissionList, boolean updatePermissions) {
        if (userPermissionList.isEmpty()) {
            return;
        }
        if (updatePermissions) {
            ((SecurityUser) userDetails).clearMap();
        }

        for (UserPermissionProjection userPermission : userPermissionList) {
            ((SecurityUser) userDetails).addPermissions(userPermission.getProjectId(), userPermission.getPermissions());
        }
    }
}
