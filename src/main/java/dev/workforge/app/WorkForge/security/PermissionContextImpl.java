package dev.workforge.app.WorkForge.security;

import dev.workforge.app.WorkForge.model.Permission;


import java.util.*;
import java.util.stream.Collectors;

class PermissionContextImpl implements PermissionContext, PermissionContextOperation {

    private final Map<Long, Set<Permission>> permissionMap = new HashMap<>();
    private long version;

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

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

    public Map<Long, Set<Permission>> getPermissionMap() {
        return permissionMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new HashSet<>(entry.getValue())
                ));
    }

    @Override
    public long getVersion() {
        return version;
    }
}
