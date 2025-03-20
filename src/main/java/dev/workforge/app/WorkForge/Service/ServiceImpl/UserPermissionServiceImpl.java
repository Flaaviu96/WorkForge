package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.DTO.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Repository.UserPermissionProjection;
import dev.workforge.app.WorkForge.Repository.UserPermissionRepository;
import dev.workforge.app.WorkForge.Security.UserSessionService;
import dev.workforge.app.WorkForge.Service.PermissionService;
import dev.workforge.app.WorkForge.Service.ProjectService;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import dev.workforge.app.WorkForge.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final ProjectService projectService;
    private final UserSessionService userSessionService;

    public UserPermissionServiceImpl(UserPermissionRepository userPermissionRepository, PermissionService permissionService, UserService userService, ProjectService projectService, UserSessionService userSessionService) {
        this.userPermissionRepository = userPermissionRepository;
        this.permissionService = permissionService;
        this.userService = userService;
        this.projectService = projectService;
        this.userSessionService = userSessionService;
    }

    @Override
    public List<UserPermissionProjection> getPermissionsForUser(String username) {
        List<UserPermissionProjection> userPermissionProjections = userPermissionRepository.findPermissionsByUser(username);
        if (userPermissionProjections.isEmpty()) {
            return List.of();
        }
        return userPermissionProjections;
    }

    @Override
    @Transactional
    public void assignProjectPermissionsForUsers(ProjectPermissionsDTO projectPermissionsDTO) {
        if (projectPermissionsDTO.permissionDTO().isEmpty()) {
            return;
        }

        List<Permission> permissionsList = permissionService.getPermissionsByPermissionType(projectPermissionsDTO.permissionDTO());
        if (permissionsList.isEmpty()) {
            return;
        }

        List<String> usernames = projectPermissionsDTO.permissionDTO().stream()
                .map(PermissionDTO::userName)
                .toList();
        List<AppUser> userList = userService.getUsersByUsernames(usernames);
        if (userList.isEmpty()) {
            return;
        }

        Optional<Project> project = projectService.getProjectByProjectId(projectPermissionsDTO.projectId());
        if (project.isEmpty()) {
            return;
        }

        for (PermissionDTO permissionDTO : projectPermissionsDTO.permissionDTO()) {
            AppUser appUser = searchBasedOnProperty(userList, appUser1 -> appUser1.getUsername().equals(permissionDTO.userName()));
            if (isPermissionAssignedForUser(appUser.getId(), permissionDTO.permissionType())) {
                continue;
            }
            UserPermission userPermission = new UserPermission();
            Permission permission = searchBasedOnProperty(permissionsList, permission1 -> permission1.getPermissionType().equals(permissionDTO.permissionType()));
            userPermission.setProject(project.get());
            userPermission.setUser(appUser);
            userPermission.addPermission(permission);
            saveUserPermission(userPermission);
        }

        for (AppUser appUser : userList) {
            userSessionService.updatePermissionSession(String.valueOf(appUser.getId()));
        }
    }

    @Override
    public void saveUserPermission(UserPermission userPermission) {
        userPermissionRepository.save(userPermission);
    }

    private <T> T searchBasedOnProperty(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).findFirst().orElse(null);
    }

    private boolean isPermissionAssignedForUser(long id, PermissionType permissionType) {
        return userPermissionRepository.isPermissionAssignedForUser(id, permissionType);
    }
}
