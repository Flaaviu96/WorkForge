package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.PermissionContext;
import dev.workforge.app.WorkForge.Security.PermissionContextOperation;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface SecurityUserService {

    /**
     * Loads user permissions from the database and adds them to the provided user details.
     *
     * @param userDetails the user details containing the permissions
     */
    void loadUserPermissionsIntoUserDetails(UserDetails userDetails);

    PermissionContext getPermissionContext();

    PermissionContextOperation getPermissionContextOperation();

    SecurityUser retrieveSecurityUser();

    PermissionContext getPermissionContext(SecurityUser user);

    PermissionContextOperation getPermissionContextOperation(SecurityUser user);
    /**
     * Refresh the user permissions with the new ones
     *
     * @param userDetails the user details containing updated permissions.
     */
    void refreshUserPermissionsForUserDetails (UserDetails userDetails);


    List<PermissionType> getProjectPermissionForUser(long projectId);
}
