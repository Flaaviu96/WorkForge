package dev.workforge.app.WorkForge.Security.SecurityImpl;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Security.PermissionContext;
import dev.workforge.app.WorkForge.Security.PermissionContextOperation;

import java.util.*;
import java.util.stream.Collectors;

public class PermissionContextImpl implements PermissionContext, PermissionContextOperation {

    private final Map<Long, Set<Permission>> permissionMap = new HashMap<>();
    private long buildPermissionAt;
    private long updatedPermission;

    @Override
    public void rebuildTimestamps() {
        long now = System.currentTimeMillis();
        this.buildPermissionAt = now;
        this.updatedPermission = now;
    }

    @Override
    public void addPermission(Long projectId, Permission permission) {
        permissionMap.computeIfAbsent(projectId, k -> new HashSet<>()).add(permission);
    }

    @Override
    public void addPermissions(Long projectId, List<Permission> permissions) {
        permissionMap.computeIfAbsent(projectId, k -> new HashSet<>()).addAll(permissions);
    }

    @Override
    public void deletePermission(Long projectId, Permission permission) {
        permissionMap.computeIfPresent(projectId, (k, perms) -> {
            perms.remove(permission);
            return perms.isEmpty() ? null : perms;
        });
    }

    @Override
    public void removeAllPermissions(Long projectId) {
        permissionMap.remove(projectId);
    }

    @Override
    public void clearMap() {
        permissionMap.clear();
    }

    public Map<Long, Set<Permission>> getPermissionMap() {
        return permissionMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new HashSet<>(entry.getValue())
                ));
    }

    @Override
    public long getUpdatedPermission() {
        return updatedPermission;
    }

    @Override
    public long getBuildPermissionAt() {
        return buildPermissionAt;
    }

    @Override
    public void setUpdatedPermission(long updatePermissionAt) {
        this.updatedPermission = updatePermissionAt;
    }

    @Override
    public void setBuildPermissionAt(long buildPermissionAt) {
        this.buildPermissionAt = buildPermissionAt;
    }
}
