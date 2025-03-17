package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserPermissionService {

     /**
      * Loads user permissions from the database and adds them to the provided user details.
      *
      * @param userDetails the user details containing the permissions
      */
     void loadUserPermissions(UserDetails userDetails);

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
     void refreshUserPermissions (UserDetails userDetails);


     /**
      * Assign the list of permissions to the users.
      *
      */
     void assignPermissionsForUsers(List<PermissionDTO> permissionDTOS);
}
