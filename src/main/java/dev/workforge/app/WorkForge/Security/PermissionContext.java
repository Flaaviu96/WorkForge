package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Model.Permission;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface PermissionContext extends Serializable {
    Map<Long, Set<Permission>> getPermissionMap();
    long getUpdatedPermission();
    long getBuildPermissionAt();
    void setUpdatedPermission(long updatePermissionAt);
    void setBuildPermissionAt(long buildPermissionAt);
}
