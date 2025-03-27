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
import java.util.function.Function;
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
        // COnsider to user array instead of List, less memory
        List<AppUser> userList = userService.getUsersByUsernames(usernames);
        if (userList.isEmpty()) {
            return;
        }

        Optional<Project> project = projectService.getProjectByProjectId(projectPermissionsDTO.projectId());
        if (project.isEmpty()) {
            return;
        }

        Map<String, Set<PermissionType>> usersPermissionsMap = groupPermissionsByUserFromDTO(projectPermissionsDTO.permissionDTO());
        List<UserPermission> userPermissions = userPermissionRepository.findByUsersIdsAndProjectId(
                userList.stream().map(AppUser::getId).toList(), project.get().getId()
        );

        Map<String, UserPermission> userPermissionsMap = userPermissions.stream()
                .collect(Collectors.toMap(up -> up.getUser().getUsername(), Function.identity()));

        List<UserPermission> saving = new ArrayList<>();
        for (Map.Entry<String, Set<PermissionType>> entry : usersPermissionsMap.entrySet()) {

            Set<Permission> newPermissions = getPermissionsByPermissionTypes(permissionsList, entry.getValue());
            UserPermission userPermission = userPermissionsMap.get(entry.getKey());

            if (userPermission == null) {
                saving.add(createUserPermission(null, project.get(), newPermissions));
                continue;
            }

            if (hasAlreadyThePermissionsAssigned(userPermissions,createPredicate(entry.getKey()), entry.getValue())) {
                continue;
            }

            userPermission.addPermissions(newPermissions);
            saving.add(userPermission);
        }
        userPermissionRepository.saveAll(saving);
        updatePermissionSession(userList);
    }

    private boolean hasAlreadyThePermissionsAssigned(List<UserPermission> userPermissions, Predicate<UserPermission> predicate, Set<PermissionType> permissionTypes) {
        return userPermissions.stream()
                .filter(predicate)
                .allMatch(
                        userPermission -> userPermission.getPermissions().stream()
                                .map(Permission::getPermissionType)
                                .collect(Collectors.toSet()).equals(permissionTypes)
                );
    }

    private void updatePermissionSession(List<AppUser> appUsers) {
        for (AppUser appUser : appUsers) {
            userSessionService.updatePermissionSession(String.valueOf(appUser.getId()));
        }
    }

    private Predicate<UserPermission> createPredicate(String username) {
        return userPermission -> userPermission.getUser().getUsername().equals(username);
    }

    private Set<Permission> getPermissionsByPermissionTypes(List<Permission> permissionsList, Set<PermissionType> permissionTypes) {
        return permissionsList.stream()
                .filter(permission -> permissionTypes.contains(permission.getPermissionType()))
                .collect(Collectors.toSet());
    }

    private UserPermission createUserPermission(AppUser appUser, Project project, Set<Permission> permissions) {
        return UserPermission.builder()
                .user(appUser)
                .project(project)
                .permissions(permissions)
                .build();
    }

    @Override
    public void saveUserPermission(UserPermission userPermission) {
        userPermissionRepository.save(userPermission);
    }
    private Map<String, Set<PermissionType>> groupPermissionsByUserFromDTO(List<PermissionDTO> permissionDTO) {
        return permissionDTO.stream()
                .collect(Collectors.groupingBy(
                        PermissionDTO::userName,
                        Collectors.mapping(PermissionDTO::permissionType, Collectors.toSet())
                ));
    }
}
