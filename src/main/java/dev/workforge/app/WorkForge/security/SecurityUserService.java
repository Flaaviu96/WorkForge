package dev.workforge.app.WorkForge.security;

import dev.workforge.app.WorkForge.model.Permission;
import dev.workforge.app.WorkForge.model.PermissionType;
import dev.workforge.app.WorkForge.projections.UserPermissionProjection;

import java.util.List;

public interface SecurityUserService {

    Context getContext();
    PermissionManager getPermissionManager();
    Maintenance getMaintenance();
    interface Context {
        UserPrincipal retrieveSecurityUser();
        List<PermissionType> getProjectPermissionForUser(long projectId);
    }

    interface PermissionManager {
        void loadPermissionsIntoPermissionContext(UserPrincipal userPrincipal, List<UserPermissionProjection> userPermissionProjections);
        void reloadPermissionsIntoPermissionContext(UserPrincipal userPrincipal, List<UserPermissionProjection> userPermissionProjections);
        void addPermission(Long projectId, Permission permission);
        void addPermissions(Long projectId, List<Permission> permissions);
        void deletePermission(Long projectId, Permission permission);
        void removeAllPermissions(Long projectId);
    }

    interface Maintenance {
        void clearMap();
        void setVersion(long version);
    }
}
