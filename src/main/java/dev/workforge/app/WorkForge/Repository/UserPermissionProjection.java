package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Permission;

import java.util.List;


public interface UserPermissionProjection {
     String getProjectKey();
     List<Permission> getPermissions();
}
