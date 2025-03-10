package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.PermissionType;

public interface UserPermissionProjection {
    public String getProjectKey();
    public PermissionType getPermission();
}
