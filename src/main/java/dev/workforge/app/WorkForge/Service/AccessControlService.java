package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.Model.PermissionType;

public interface AccessControlService {

     boolean hasPermissions(Long projectId, PermissionType[] permissionTypes, String sessionId);

     int[] getAvailableProjectsForCurrentUser();
}
