package dev.workforge.app.WorkForge.Security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.workforge.app.WorkForge.Model.Permission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SecurityUser extends UserDetails {

    Map<Long, Set<Permission>> getPermissionMap();

    long getId();

    long getLastPermissionsUpdate();

    void addPermission(Long projectId, Permission permission);

    void addPermissions(Long projectId, List<Permission> permissions);

    void deletePermission(Long projectId, Permission permission);

    void removeAllPermissions(Long projectId);

    void clearMap();

    @Override
    @JsonIgnore
    default boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    @JsonIgnore
    default boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    @JsonIgnore
    default boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    @JsonIgnore
    default boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    @JsonIgnore
    default Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
