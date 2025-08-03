package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Model.Permission;

import java.util.List;

public interface PermissionContextOperation {
    void rebuildTimestamps();
    void addPermission(Long projectId, Permission permission);
    void addPermissions(Long projectId, List<Permission> permissions);
    void deletePermission(Long projectId, Permission permission);
    void removeAllPermissions(Long projectId);
    void clearMap();
}