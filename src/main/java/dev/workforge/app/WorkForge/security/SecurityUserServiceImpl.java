package dev.workforge.app.WorkForge.security;

import dev.workforge.app.WorkForge.model.Permission;
import dev.workforge.app.WorkForge.model.PermissionType;
import dev.workforge.app.WorkForge.projections.UserPermissionProjection;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecurityUserServiceImpl implements SecurityUserService {

    private final PermissionManager permissionManager = new PermissionManager();
    private final Context context = new Context();
    private final Maintenance maintenance = new Maintenance();

    @Override
    public SecurityUserService.Context getContext() {
        return context;
    }

    @Override
    public SecurityUserService.PermissionManager getPermissionManager() {
        return permissionManager;
    }

    @Override
    public SecurityUserService.Maintenance getMaintenance() {
        return maintenance;
    }

    private class PermissionManager implements SecurityUserService.PermissionManager {

        @Override
        public void loadPermissionsIntoPermissionContext(UserPrincipal userPrincipal, List<UserPermissionProjection> userPermissionProjections) {
            managePermissionsInPermissionContext(userPermissionProjections, false);
        }

        private void managePermissionsInPermissionContext(List<UserPermissionProjection> userPermissionList, boolean updatePermissions) {
            if (updatePermissions) {
                getPermissionContextOperation().clearMap();
            }

            if (userPermissionList.isEmpty()) {
                return;
            }

            for (UserPermissionProjection userPermission : userPermissionList) {
                getPermissionContextOperation().addPermissions(userPermission.getProjectId(), userPermission.getPermissions());
            }
        }

        @Override
        public void reloadPermissionsIntoPermissionContext(UserPrincipal userPrincipal, List<UserPermissionProjection> userPermissionProjections) {
            managePermissionsInPermissionContext(userPermissionProjections, true);
        }

        @Override
        public void addPermission(Long projectId, Permission permission) {
            getPermissionContextOperation().addPermission(projectId, permission);
        }

        @Override
        public void addPermissions(Long projectId, List<Permission> permissions) {
            getPermissionContextOperation().addPermissions(projectId, permissions);
        }

        @Override
        public void deletePermission(Long projectId, Permission permission) {

        }

        @Override
        public void removeAllPermissions(Long projectId) {

        }
    }

    private class Context implements SecurityUserService.Context {

        @Override
        public UserPrincipal retrieveSecurityUser() {
            return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }

        @Override
        public List<PermissionType> getProjectPermissionForUser(long projectId) {
            return List.of();
        }
    }

    private class Maintenance implements SecurityUserService.Maintenance {

        @Override
        public void clearMap() {

        }

        @Override
        public void setVersion(long version) {
            getPermissionContextOperation().setVersion(version);
        }

    }

    private PermissionContext getPermissionContext() {
        return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPermissionContext();
    }

    private PermissionContextOperation getPermissionContextOperation() {
        Object permissionContext = getPermissionContext();
        if (permissionContext instanceof PermissionContextOperation permissionContextOperation) {
            return permissionContextOperation;
        }
        throw new IllegalStateException("Principal does not implement PermissionContextOperation");
    }
}
