package dev.workforge.app.WorkForge.Security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.workforge.app.WorkForge.Model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import dev.workforge.app.WorkForge.Model.Permission;
import java.util.*;


public class SecurityUser implements UserDetails {
    private final String username;
    private final String password;
    private Map<Long, Set<Permission>> permissionMap = new HashMap<>();

    public SecurityUser(AppUser user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    @JsonCreator
    public SecurityUser(@JsonProperty("username") String username,
                        @JsonProperty("password") String password,
                        @JsonProperty("permissionMap") Map<Long, Set<Permission>> permissionMap) {
        this.username = username;
        this.password = password;
        this.permissionMap = permissionMap != null ? permissionMap : new HashMap<>();
    }


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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
        return permissionMap;
    }

    public void addPermission(Long projectId, List<Permission> permissions) {
        permissionMap.computeIfAbsent(projectId, k -> new HashSet<>()).addAll(permissions);
    }

    public void addPermission(Long projectId, Permission permissions) {
        permissionMap.computeIfAbsent(projectId, k -> new HashSet<>()).add(permissions);
    }

    public void deletePermission(Long projectId, Permission permission) {
        permissionMap.computeIfPresent(projectId, (k, permissions) ->{
           permissions.remove(permission);
           return permissions.isEmpty() ? null : permissions;
        });
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    public void removeAllPermissions(Long projectId) {
        permissionMap.remove(projectId);
    }
}
