package dev.workforge.app.WorkForge.Security.SecurityUser;

import dev.workforge.app.WorkForge.Model.Permission;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface PermissionContext extends Serializable {

    /**
     * Returns a map of permissions grouped by project ID.
     * Each key is a project ID, and the corresponding value is the set of Permissions
     * assigned to the user for that project.
     *
     * @return a map where keys are project IDs and values are sets of Permissions
     */
    Map<Long, Set<Permission>> getPermissionMap();

    /**
     * Returns the timestamp (in milliseconds or another defined unit) indicating when the permissions
     * were last updated.
     *
     * @return the timestamp of the last permissions update
     */
    long getUpdatedPermission();

    /**
     * Returns the timestamp indicating when the permission context was last built or constructed.
     *
     * @return the timestamp when the permission context was built
     */
    long getBuildPermissionAt();

    /**
     * Sets the timestamp indicating when the permissions were last updated.
     *
     * @param updatePermissionAt the new timestamp for the last permissions update
     */
    void setUpdatedPermission(long updatePermissionAt);

    /**
     * Sets the timestamp indicating when the permission context was last built.
     *
     * @param buildPermissionAt the new timestamp for when the permission context was built
     */
    void setBuildPermissionAt(long buildPermissionAt);
}
