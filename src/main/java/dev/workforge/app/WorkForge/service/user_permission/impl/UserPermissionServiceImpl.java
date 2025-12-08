package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.dto.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.exceptions.UserException;
import dev.workforge.app.WorkForge.model.*;
import dev.workforge.app.WorkForge.projections.UserPermissionProjection;
import dev.workforge.app.WorkForge.service.user_permission.PermissionService;
import dev.workforge.app.WorkForge.service.user_permission.UserPermissionService;
import dev.workforge.app.WorkForge.service.user_permission.UserService;
import dev.workforge.app.WorkForge.service.usersession.impl.PermissionVersionService;
import dev.workforge.app.WorkForge.util.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Service implementation for managing user permissions within a project.
 * This class handles assigning, removing, and saving user permissions for specific projects.
 * It interacts with the repository layer and other services to fetch and persist data.
 */
@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(UserPermissionServiceImpl.class);

    private final UserPermissionFacadeService userPermissionFacadeService;
    private final UserPermissionDataFetcher userPermissionDataFetcher;
    private final PermissionService permissionService;
    private final UserService userService;
    private final PermissionVersionService permissionVersionService;

    /**
     *
     * @param permissionService        Service for managing permissions.
     * @param userService              Service for managing users.
     */
    public UserPermissionServiceImpl(
            UserPermissionFacadeService userPermissionFacadeService,
            UserPermissionDataFetcher dataFetcher,
            PermissionService permissionService,
            UserService userService,
            PermissionVersionService permissionVersionService
    ) {
        this.userPermissionFacadeService = userPermissionFacadeService;
        this.userPermissionDataFetcher = dataFetcher;
        this.permissionService = permissionService;
        this.userService = userService;
        this.permissionVersionService = permissionVersionService;
    }


    @Override
    public List<UserPermissionProjection> getPermissionsForUser(String username) {
        return userPermissionDataFetcher.getPermissionsForUser(username);
    }

    @Override
    public void manageProjectPermissionsForUsers(ProjectPermissionsDTO projectPermissionsDTO) {
        userPermissionFacadeService.managePermissions(projectPermissionsDTO);
    }

    @Override
    public void removeProjectPermissions(ProjectPermissionsDTO projectPermissionsDTO) {
        userPermissionFacadeService.removePermissions(projectPermissionsDTO);
    }


    @Override
    public void createDefaultOwnerPermissions(UUID user, Project project) {
        AppUser appUser = userService.getUserByUUID(user);
        if (appUser == null) {
            throw new UserException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        List<PermissionType> defaultTypes = List.of(PermissionType.READ, PermissionType.WRITE, PermissionType.PROJECT_ADMIN);
        List<Permission> permissions = permissionService.getPermissionsByPermissionType(defaultTypes);

        UserPermission userPermission = new UserPermission();
        userPermission.setUser(appUser);
        userPermission.setProject(project);
        userPermission.setPermissions(new HashSet<>(permissions));
        permissionVersionService.increment(appUser.getUsername());
        userPermissionFacadeService.saveUserPermission(userPermission);
    }
}