package dev.workforge.app.WorkForge.Security.SecurityImpl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.workforge.app.WorkForge.Model.AppUser;
import dev.workforge.app.WorkForge.Security.SecurityUser.PermissionContext;
import dev.workforge.app.WorkForge.Security.SecurityUser.SecurityUser;


public class SecurityUserImpl implements SecurityUser {

    private final long id;
    private final String username;
    private final String password;
    private final PermissionContext permissionContext;

    public SecurityUserImpl(AppUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.permissionContext = new PermissionContextImpl(); // initialize here!
        //lastPermissionsUpdate = System.currentTimeMillis();
    }

    @JsonCreator
    public SecurityUserImpl(
                        @JsonProperty("id") long id,
                        @JsonProperty("username") String username,
                        @JsonProperty("password") String password,
                        @JsonProperty("permissionContext") PermissionContext permissionContext
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.permissionContext = permissionContext;
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

    public long getId() {
        return id;
    }

    @Override
    public PermissionContext getPermissionContext() {
        return permissionContext;
    }

}
