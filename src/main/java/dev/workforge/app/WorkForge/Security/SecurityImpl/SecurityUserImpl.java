package dev.workforge.app.WorkForge.Security.SecurityImpl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.workforge.app.WorkForge.Model.AppUser;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Model.Permission;
import java.util.*;
import java.util.stream.Collectors;


public class SecurityUserImpl implements SecurityUser {
    private final long id;
    private final String username;
    private final String password;
    private Map<Long, Set<Permission>> permissionMap = new HashMap<>();
    private long lastPermissionsUpdate;

    public SecurityUserImpl(AppUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        lastPermissionsUpdate = System.currentTimeMillis();
    }

    @JsonCreator
    public SecurityUserImpl(
                        @JsonProperty("id") long id,
                        @JsonProperty("username") String username,
                        @JsonProperty("password") String password,
                        @JsonProperty("permissionMap") Map<Long, Set<Permission>> permissionMap,
                        @JsonProperty("permissionTime") long lastPermissionsUpdate
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.permissionMap = permissionMap != null ? permissionMap : new HashMap<>();
        this.lastPermissionsUpdate = lastPermissionsUpdate;
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

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Map<Long, Set<Permission>> getPermissionMap() {
        return permissionMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new HashSet<>(entry.getValue())
                ));
    }

    public long getId() {
        return id;
    }

    public long getLastPermissionsUpdate() {
        return lastPermissionsUpdate;
    }
}
