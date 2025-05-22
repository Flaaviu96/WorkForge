package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.Model.UserPermission;
import dev.workforge.app.WorkForge.Projections.UserPermissionProjection;

import java.util.List;

public interface UserPermissionService {

     /**
      * Fetching the permissions for the current user
      * @param username the username of the user
      * @return the list of the permissions or a empty list if the user doesn't have any permissions
      */
     List<UserPermissionProjection> getPermissionsForUser(String username);

     /**
      * Assign the list of permissions to the users.
      *
      */
     void updateProjectPermissionsForUsers(ProjectPermissionsDTO projectPermissionsDTO);

     /**
      * Saving the UserPermission model into the database
      * @param userPermission is the model
      */
     void saveUserPermission(UserPermission userPermission);
}
