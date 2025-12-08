package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.model.*;
import dev.workforge.app.WorkForge.service.user_permission.UserPermissionRepositoryService;
import dev.workforge.app.WorkForge.service.usersession.impl.PermissionVersionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPermissionAssignmentService extends AbstractUserPermission{


    public UserPermissionAssignmentService(
            UserPermissionRepositoryService userPermissionRepositoryService,
            UserPermissionDataFetcher userPermissionDataFetcher,
            PermissionUtils permissionUtils,
            PermissionVersionService permissionVersionService) {
        super(userPermissionRepositoryService, userPermissionDataFetcher, permissionUtils, permissionVersionService);
    }

    protected void handlePermissions(
            PermissionData data,
            Map<Long, Set<PermissionType>> permissionsFromDTO,
            Map<Long, UserPermission> existingPermissions
    ) {
        List<UserPermission> newPermissionsToSave = new ArrayList<>();

        for (var entry : permissionsFromDTO.entrySet()) {
            Long userId = entry.getKey();
            Set<Permission> newPerms =
                    permissionUtils.getPermissionsByPermissionTypes(data.permissionsList(), entry.getValue());

            UserPermission existing = existingPermissions.get(userId);

            AppUser user = data.userList().stream()
                    .filter(u -> u.getId() == userId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            if (existing == null) {
                newPermissionsToSave.add(createUserPermission(user, data.project(), newPerms));
            } else if (!existing.getPermissions().containsAll(newPerms)) {
                existing.addPermissions(newPerms);
            }
        }

        if (!newPermissionsToSave.isEmpty()) {
            saveAll(newPermissionsToSave);
        }

    }

    private UserPermission createUserPermission(AppUser appUser, Project project, Set<Permission> permissions) {
        return UserPermission.builder()
                .user(appUser)
                .project(project)
                .permissions(permissions)
                .build();
    }
}
