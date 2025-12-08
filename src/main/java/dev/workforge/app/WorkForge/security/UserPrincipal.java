package dev.workforge.app.WorkForge.security;



public class UserPrincipal {
    private final String username;
    private final long id;
    private final PermissionContext permissionContext;

    public UserPrincipal(String username, long id) {
        this.username = username;
        this.id = id;
        this.permissionContext = new PermissionContextImpl();
    }

    public String getUsername() {
        return username;
    }

    public PermissionContext getPermissionContext() {
        return permissionContext;
    }

    public long getId() {
        return id;
    }
}
