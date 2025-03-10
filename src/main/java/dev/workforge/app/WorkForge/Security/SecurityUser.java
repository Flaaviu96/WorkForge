package dev.workforge.app.WorkForge.Security;


import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


public class SecurityUser implements UserDetails {
    private final AppUser user;
    private final Map<String, Set<Permission>> permissionMap = new HashMap<>();

    public SecurityUser(AppUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public Map<String, Set<Permission>> getPermissionMap() {
        return permissionMap;
    }

    public void addPermission(String projectKey, Set<Permission> permissions) {
        permissionMap.computeIfAbsent(projectKey, k -> new HashSet<>()).addAll(permissions);
    }

    public void addPermission(String projectKey, Permission permissions) {
        permissionMap.computeIfAbsent(projectKey, k -> new HashSet<>()).add(permissions);
    }

    public void deletePermission(String projectKey, Permission permission) {
        permissionMap.computeIfPresent(projectKey, (k, permissions) ->{
           permissions.remove(permission);
           return permissions.isEmpty() ? null : permissions;
        });
    }

    public void removeAllPermissions(String projectKey) {
        permissionMap.remove(projectKey);
    }
}
