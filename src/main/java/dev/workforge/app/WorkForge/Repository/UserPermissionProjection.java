package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Permission;

public interface UserPermissionProjection {
    public String getProjectKey();
    public Permission getPermission();
}
