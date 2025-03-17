package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;

import java.util.List;

public interface PermissionService {
    List<Permission> getPermissionsByIds(List<PermissionType> permissionIds);
}
