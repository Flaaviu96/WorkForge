package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.PermissionContext;
import dev.workforge.app.WorkForge.Security.PermissionContextOperation;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface SecurityUserService {

    /**
     * Loads and attaches user permissions from the database into the given UserDetails object.
     * This enriches the UserDetails with up-to-date permission information for authorization purposes.
     *
     * @param userDetails the UserDetails instance to load permissions into
     */
    void loadUserPermissionsIntoUserDetails(UserDetails userDetails);

    /**
     * Retrieves the current permission context for the logged-in user.
     *
     * @return the PermissionContext representing the user's permission scope
     */
    PermissionContext getPermissionContext();

    /**
     * Retrieves the current permission context operation for the logged-in user.
     *
     * @return the PermissionContextOperation indicating what operations the user can perform
     */
    PermissionContextOperation getPermissionContextOperation();

    /**
     * Retrieves the SecurityUser object representing the currently authenticated user.
     *
     * @return the SecurityUser instance of the authenticated user
     */
    SecurityUser retrieveSecurityUser();

    /**
     * Retrieves the permission context for a specific SecurityUser.
     *
     * @param user the SecurityUser for whom the permission context is fetched
     * @return the PermissionContext representing the user's permission scope
     */
    PermissionContext getPermissionContext(SecurityUser user);

    /**
     * Retrieves the permission context operation for a specific SecurityUser.
     *
     * @param user the SecurityUser for whom the permission context operation is fetched
     * @return the PermissionContextOperation indicating the operations the user can perform
     */
    PermissionContextOperation getPermissionContextOperation(SecurityUser user);

    /**
     * Refreshes the permissions of the given UserDetails object, updating it with the latest permissions.
     *
     * @param userDetails the UserDetails whose permissions are to be refreshed
     */
    void refreshUserPermissionsForUserDetails(UserDetails userDetails);

    /**
     * Retrieves the list of PermissionType values the user has for a specific project.
     *
     * @param projectId the ID of the project for which permissions are queried
     * @return a list of PermissionType enums representing the user's permissions on the project
     */
    List<PermissionType> getProjectPermissionForUser(long projectId);
}
