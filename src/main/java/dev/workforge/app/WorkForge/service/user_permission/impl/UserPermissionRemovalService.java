package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.model.*;
import dev.workforge.app.WorkForge.service.user_permission.UserPermissionRepositoryService;
import dev.workforge.app.WorkForge.service.usersession.impl.PermissionVersionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPermissionRemovalService extends AbstractUserPermission {


    public UserPermissionRemovalService(
            UserPermissionRepositoryService userPermissionRepositoryService,
            UserPermissionDataFetcher userPermissionDataFetcher,
            PermissionUtils permissionUtils,
            PermissionVersionService permissionVersionService
            ) {
        super(userPermissionRepositoryService, userPermissionDataFetcher, permissionUtils, permissionVersionService);
    }

    @Override
    protected void handlePermissions(PermissionData data, Map<Long, Set<PermissionType>> permissionsFromDTO, Map<Long, UserPermission> existingPermissions) {
        List<UserPermission> permissionsToDelete = new ArrayList<>();
        List<UserPermission> permissionsToRemoveCompletely = new ArrayList<>();
        for (Map.Entry<Long, Set<PermissionType>> entry : permissionsFromDTO.entrySet()) {

            Set<Permission> removePermissions = permissionUtils.getPermissionsByPermissionTypes(data.permissionsList(), entry.getValue());
            UserPermission userPermission = existingPermissions.get(entry.getKey());
            if (userPermission != null) {
                handlePermissionsRemoval(userPermission, removePermissions, permissionsToDelete, permissionsToRemoveCompletely);
            }
        }

        if (!permissionsToRemoveCompletely.isEmpty()) {
            userPermissionRepositoryService.deleteAll(permissionsToRemoveCompletely);
        }

        if (!permissionsToDelete.isEmpty() || !permissionsToRemoveCompletely.isEmpty()) {
            List<UserPermission> affectedPermissions = new ArrayList<>();
            affectedPermissions.addAll(permissionsToDelete);
            affectedPermissions.addAll(permissionsToRemoveCompletely);
            //updatePermissionSession(dataResult.userList, affectedPermissions);
        }
    }

    /**
     * Handles the logic of removing the permissions to an existing permission record.
     *
     *
     * @param userPermission The existing UserPermission object retrieved from the database.
     * @param permissionsToRemove The permissions that are to be deleted from the user.
     * @param permissionsToRemoveList A list to accumulate the permissions that need to be deleted from the database.
     */
    private void handlePermissionsRemoval(UserPermission userPermission, Set<Permission> permissionsToRemove,
                                          List<UserPermission> permissionsToRemoveList, List<UserPermission> permissionsToRemoveCompletely) {
        userPermission.removePermisisons(permissionsToRemove);

        if(userPermission.getPermissions().isEmpty()) {
            permissionsToRemoveCompletely.add(userPermission);
        } else {
            permissionsToRemoveList.add(userPermission);
        }
    }
}
