package dev.workforge.app.WorkForge.Service.UserPermission;

import dev.workforge.app.WorkForge.DTO.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.Model.Project;
import dev.workforge.app.WorkForge.Projections.UserPermissionProjection;

import java.util.List;
import java.util.UUID;

public interface UserPermissionService {

     /**
      * Retrieves all permissions associated with a given user.
      *
      * @param username the username of the user whose permissions are being fetched
      * @return a list of UserPermissionProjection objects representing the user's permissions,
      *         or an empty list if the user has no permissions
      */
     List<UserPermissionProjection> getPermissionsForUser(String username);

     /**
      * Updates the permissions assigned to multiple users for a specific project.
      * This method processes the ProjectPermissionsDTO which contains the necessary
      * data to assign or modify permissions for users.
      *
      * @param projectPermissionsDTO Data Transfer Object containing project and users permissions data
      */
     void updateProjectPermissionsForUsers(ProjectPermissionsDTO projectPermissionsDTO);

     /**
      * Creates default owner permissions for a given user on a specific project.
      * Typically used when a new project is created to grant the project owner full permissions.
      *
      * @param user the UUID of the user who will receive owner permissions
      * @param project the Project entity to which the permissions apply
      */
     void createDefaultOwnerPermissions(UUID user, Project project);

}
