package dev.workforge.app.WorkForge.Service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface SecurityUserService {

    /**
     * Loads user permissions from the database and adds them to the provided user details.
     *
     * @param userDetails the user details containing the permissions
     */
    void loadUserPermissionsIntoUserDetails(UserDetails userDetails);

    /**
     * Extracts the IDs of projects that do not have a specific permission type (e.g., READ, WRITE).
     *
     * @return a list of project IDs.
     */
    List<Long> extractProjectIdsFromSecurityContext();

    /**
     * Refresh the user permissions with the new ones
     *
     * @param userDetails the user details containing updated permissions.
     */
    void refreshUserPermissionsForUserDetails (UserDetails userDetails);
}
