package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.dto.PermissionDTO;
import dev.workforge.app.WorkForge.dto.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.exceptions.PermissionException;
import dev.workforge.app.WorkForge.exceptions.UserException;
import dev.workforge.app.WorkForge.model.AppUser;
import dev.workforge.app.WorkForge.model.Permission;
import dev.workforge.app.WorkForge.model.Project;
import dev.workforge.app.WorkForge.projections.UserPermissionProjection;
import dev.workforge.app.WorkForge.service.project.ProjectReadService;
import dev.workforge.app.WorkForge.service.user_permission.PermissionService;
import dev.workforge.app.WorkForge.service.user_permission.UserPermissionRepositoryService;
import dev.workforge.app.WorkForge.service.user_permission.UserService;
import dev.workforge.app.WorkForge.util.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserPermissionDataFetcher {

    private final PermissionService permissionService;
    private final UserService userService;
    private final ProjectReadService projectService;
    private final UserPermissionRepositoryService userPermissionRepositoryService;

    public UserPermissionDataFetcher(PermissionService permissionService, UserService userService, ProjectReadService projectService, UserPermissionRepositoryService userPermissionRepositoryService) {
        this.permissionService = permissionService;
        this.userService = userService;
        this.projectService = projectService;
        this.userPermissionRepositoryService = userPermissionRepositoryService;
    }

    public List<UserPermissionProjection> getPermissionsForUser(String username) {
        List<UserPermissionProjection> userPermissionProjections = userPermissionRepositoryService.findPermissionsByUser(username);
        if (userPermissionProjections.isEmpty()) {
            return List.of();
        }
        return userPermissionProjections;
    }

    public PermissionData fetchingRequiredData(ProjectPermissionsDTO projectPermissionsDTO) {
        if (projectPermissionsDTO.permissionDTO().isEmpty()) {
            return null;
        }

        List<Permission> permissionsList = permissionService.getPermissionsByDTO(projectPermissionsDTO.permissionDTO());
        if (permissionsList.isEmpty()) {
            throw new PermissionException(ErrorMessages.PERMISSIONS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        List<Long> usersIds = projectPermissionsDTO.permissionDTO().stream()
                .map(PermissionDTO::userId)
                .toList();

        List<AppUser> userList = userService.getUsersByIds(usersIds);
        if (userList.isEmpty()) {
            throw new UserException(ErrorMessages.USERS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Optional<Project> project = projectService.getProjectByProjectId(projectPermissionsDTO.projectId());
        return project.map(value -> new PermissionData(permissionsList, userList, value)).orElse(null);
    }
}
