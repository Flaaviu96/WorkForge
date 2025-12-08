package dev.workforge.app.WorkForge.security;

import dev.workforge.app.WorkForge.model.Permission;

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

    long getVersion();

}
