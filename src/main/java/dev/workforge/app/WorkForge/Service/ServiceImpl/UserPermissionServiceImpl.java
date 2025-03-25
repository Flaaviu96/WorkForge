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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(UserPermissionServiceImpl.class);

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
            logger.warn("The permissions are not present in the database.");
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
        Map<Long, Set<UserPermission>> userPermissionMap = getUserPermissionsMap(userList);
        for (PermissionDTO permissionDTO : projectPermissionsDTO.permissionDTO()) {
            AppUser appUser = searchBasedOnProperty(userList, appUser1 -> appUser1.getUsername().equals(permissionDTO.userName()));
            if (isPermissionAssignedForUser(project.get().getId(), appUser.getId(), permissionDTO.permissionType(), userPermissionMap)) {
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

    private boolean isPermissionAssignedForUser(long id, long userId, PermissionType permissionType, Map<Long, Set<UserPermission>> userPermissionMap) {
        return userPermissionMap.getOrDefault(id, Set.of()).stream()
                .anyMatch(userPermission -> userPermission.getUser().getId() == userId && userPermission.getPermissions()
                        .stream()
                        .anyMatch(permission -> permission.getPermissionType() == permissionType)
                );
    }

    private Map<Long, Set<UserPermission>> getUserPermissionsMap (List<AppUser> userList) {
        List<UserPermission> userPermissions = userPermissionRepository.findByUserIds(userList.stream().map(AppUser::getId).toList());
        return userPermissions.stream().collect(Collectors.groupingBy(
                up -> up.getProject().getId(),
                Collectors.toSet()));
    }
}
