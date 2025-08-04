package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Model.Permission;

import java.util.List;

public interface PermissionContextOperation {

    /**
     * Rebuilds or updates internal timestamps related to the permission context.
     * Typically used to refresh metadata such as last updated or built timestamps.
     */
    void rebuildTimestamps();

    /**
     * Adds a single permission to the set of permissions for the specified project.
     *
     * @param projectId the ID of the project to which the permission will be added
     * @param permission the Permission object to add
     */
    void addPermission(Long projectId, Permission permission);

    /**
     * Adds multiple permissions to the set of permissions for the specified project.
     *
     * @param projectId the ID of the project to which the permissions will be added
     * @param permissions a list of Permission objects to add
     */
    void addPermissions(Long projectId, List<Permission> permissions);

    /**
     * Deletes a specific permission from the set of permissions for the specified project.
     *
     * @param projectId the ID of the project from which the permission will be deleted
     * @param permission the Permission object to delete
     */
    void deletePermission(Long projectId, Permission permission);

    /**
     * Removes all permissions associated with the specified project.
     *
     * @param projectId the ID of the project whose permissions will be removed
     */
    void removeAllPermissions(Long projectId);

    /**
     * Clears the entire permission map, removing all permissions for all projects.
     */
    void clearMap();
}
