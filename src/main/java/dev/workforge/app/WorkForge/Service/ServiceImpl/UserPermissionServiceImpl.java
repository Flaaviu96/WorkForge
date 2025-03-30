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
import java.util.stream.Collectors;

/**
 * Service implementation for managing user permissions within a project.
 * This class handles assigning, removing, and saving user permissions for specific projects.
 * It interacts with the repository layer and other services to fetch and persist data.
 */
@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(UserPermissionServiceImpl.class);

    private final UserPermissionRepository userPermissionRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final ProjectService projectService;
    private final UserSessionService userSessionService;

    /**
     *
     * @param userPermissionRepository Repository for user permissions.
     * @param permissionService        Service for managing permissions.
     * @param userService              Service for managing users.
     * @param projectService           Service for managing projects.
     * @param userSessionService       Service for managing user sessions.
     */
    public UserPermissionServiceImpl(UserPermissionRepository userPermissionRepository, PermissionService permissionService, UserService userService, ProjectService projectService, UserSessionService userSessionService) {
        this.userPermissionRepository = userPermissionRepository;
        this.permissionService = permissionService;
        this.userService = userService;
        this.projectService = projectService;
        this.userSessionService = userSessionService;
    }

    /**
     *
     * @param username the username of the user whose permissions will be retrieved
     * @return a list of UserPermissionProjection objects for given user or an empty list if is not found
     */
    @Override
    public List<UserPermissionProjection> getPermissionsForUser(String username) {
        List<UserPermissionProjection> userPermissionProjections = userPermissionRepository.findPermissionsByUser(username);
        if (userPermissionProjections.isEmpty()) {
            return List.of();
        }
        return userPermissionProjections;
    }

    /**
     * Assigns project permissions to multiple users based on the provided ProjectPermissionsDTO.
     * This method creates new permissions or updates existing ones for users in a specific project.
     *
     * @param projectPermissionsDTO The DTO containing the project and user permissions to assign.
     */
    @Override
    @Transactional
    public void assignProjectPermissionsForUsers(ProjectPermissionsDTO projectPermissionsDTO) {
        PermissionData dataResult = fetchingRequiredData(projectPermissionsDTO);
        if (dataResult == null) {
            return;
        }
        Map<Long, Set<PermissionType>> usersPermissionsMapFromDTO = groupPermissionsByUserIdFromDTO(projectPermissionsDTO.permissionDTO());
        List<UserPermission> userPermissions = userPermissionRepository.findByUsersIdsAndProjectId(
                dataResult.userList.stream().map(AppUser::getId).toList(), dataResult.project.getId()
        );

        Map<Long, UserPermission> userPermissionsMapFromDB = userPermissions.stream()
                .collect(Collectors.toMap(up -> up.getUser().getId(), Function.identity()));

        List<UserPermission> permissionsToSave = new ArrayList<>();
        for (Map.Entry<Long, Set<PermissionType>> entry : usersPermissionsMapFromDTO.entrySet()) {

            Set<Permission> newPermissions = getPermissionsByPermissionTypes(dataResult.permissionsList, entry.getValue());
            UserPermission userPermission = userPermissionsMapFromDB.get(entry.getKey());

            AppUser appUser = dataResult.userList.stream()
                    .filter(user -> user.getId() == entry.getKey())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + entry.getKey()));

            if (userPermission == null) {
                permissionsToSave.add(createUserPermission(appUser, dataResult.project, newPermissions));
            } else {
                handleExistingPermission(entry.getKey(), userPermissions, userPermission, newPermissions, permissionsToSave);
            }
        }
        updatePermissionSession(dataResult.userList, permissionsToSave);
    }

    /**
     * Assigns project permissions to multiple users based on the provided ProjectPermissionsDTO.
     * This method removes permissions for users in a specific project.
     *
     *@param projectPermissionsDTO The DTO containing the project and user permissions to assign.
     */
    @Transactional
    public void removeProjectPermissionsFromUsers(ProjectPermissionsDTO projectPermissionsDTO) {
        PermissionData dataResult = fetchingRequiredData(projectPermissionsDTO);
        if (dataResult == null) {
            return;
        }
        Map<Long, Set<PermissionType>> usersPermissionsMap = groupPermissionsByUserIdFromDTO(projectPermissionsDTO.permissionDTO());
        List<UserPermission> userPermissionsListFromDB = userPermissionRepository.findByUsersIdsAndProjectId(
                dataResult.userList.stream().map(AppUser::getId).toList(), dataResult.project.getId()
        );

        Map<Long, UserPermission> userPermissionsMapFromDB = userPermissionsListFromDB.stream()
                .collect(Collectors.toMap(up -> up.getUser().getId(), Function.identity()));

        List<UserPermission> permissionsToDelete = new ArrayList<>();
        List<UserPermission> permissionsToRemoveCompletely = new ArrayList<>();
        for (Map.Entry<Long, Set<PermissionType>> entry : usersPermissionsMap.entrySet()) {

            Set<Permission> removePermissions = getPermissionsByPermissionTypes(dataResult.permissionsList, entry.getValue());
            UserPermission userPermission = userPermissionsMapFromDB.get(entry.getKey());
            if (userPermission != null) {
                handlePermissionsRemoval(userPermission, removePermissions, permissionsToDelete, permissionsToRemoveCompletely);
            }
        }
        if (!permissionsToRemoveCompletely.isEmpty()) {
            userPermissionRepository.deleteAll(permissionsToRemoveCompletely);
        }

        if (!permissionsToDelete.isEmpty() || !permissionsToRemoveCompletely.isEmpty()) {
            List<UserPermission> affectedPermissions = new ArrayList<>();
            affectedPermissions.addAll(permissionsToDelete);
            affectedPermissions.addAll(permissionsToRemoveCompletely);
            updatePermissionSession(dataResult.userList, affectedPermissions);
        }
    }

    /**
     * Handles the logic for adding new permissions to an existing permission record.
     * Checks if the new permissions are already present for the user and, if not, adds them to the record.
     *
     * @param userId               The ID of the user to whom the permissions will be assigned.
     * @param userPermissionsList  The list of current UserPermission objects from the database for the user.
     * @param existingPermission   The existing UserPermission object retrieved from the database.
     * @param newPermissions       The new permissions that are to be assigned to the user.
     * @param permissionsToSave    A list to accumulate the permissions that need to be saved to the database.
     */
    private void handleExistingPermission(long userId, List<UserPermission> userPermissionsList, UserPermission existingPermission, Set<Permission> newPermissions, List<UserPermission> permissionsToSave) {
        if (!hasPermissionsAlreadyAssigned(userPermissionsList, userId, newPermissions)) {
            existingPermission.addPermissions(newPermissions);
            permissionsToSave.add(existingPermission);
        }
    }

    /**
     * Handles the logic of removing the permissions to an existing permission record.
     *
     *
     * @param userPermission The existing UserPermission object retrieved from the database.
     * @param permissionsToRemove The permissions that are to be deleted from the user.
     * @param permissionsToRemoveList A list to accumulate the permissions that need to be deleted from the database.
     */
    private void handlePermissionsRemoval(UserPermission userPermission, Set<Permission> permissionsToRemove,
                                          List<UserPermission> permissionsToRemoveList, List<UserPermission> permissionsToRemoveCompletely) {
        userPermission.removePermisisons(permissionsToRemove);

        if(userPermission.getPermissions().isEmpty()) {
            permissionsToRemoveCompletely.add(userPermission);
        } else {
            permissionsToRemoveList.add(userPermission);
        }
    }

    /**
     * Checks if the user already has all the specified new permissions assigned in their permission record.
     *
     * @param userPermissions   The list of UserPermission objects for the current user.
     * @param userId            The ID of the user whose permissions are being checked.
     * @param newPermissions    The new permissions to compare against the existing ones.
     */
    private boolean hasPermissionsAlreadyAssigned(List<UserPermission> userPermissions, long userId, Set<Permission> newPermissions) {
        return userPermissions.stream()
                .filter(up -> up.getUser().getId() == userId)
                .anyMatch(up -> up.getPermissions().containsAll(newPermissions));
    }

    private void updatePermissionSession(List<AppUser> appUsers, List<UserPermission> removing) {
        List<AppUser> appUsersUpdated = appUsers.stream()
                .filter(appUser -> removing.stream()
                        .anyMatch(userPermission -> userPermission.getUser().getId() == appUser.getId()))
                .toList();
        for (AppUser appUser : appUsersUpdated) {
            if (userSessionService.hasKey(appUser.getUsername())) {
                userSessionService.updatePermissionSession(String.valueOf(appUser.getId()));
            }
        }
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

    /**
     * Helper method that groups permissions by user ID from the provided list of PermissionDTO objects.
     *
     * @param permissionDTO List of PermissionDTO objects containing permission information.
     * @return A map where the key is the user ID and the value is a set of permission types assigned to that user.
     */
    private Map<Long, Set<PermissionType>> groupPermissionsByUserIdFromDTO(List<PermissionDTO> permissionDTO) {
        return permissionDTO.stream()
                .collect(Collectors.groupingBy(
                        PermissionDTO::userId,
                        Collectors.mapping(PermissionDTO::permissionType, Collectors.toSet())
                ));
    }

    /**
     * Fetches the required data (permissions, users, and project) from various services based on the provided ProjectPermissionsDTO.
     *
     * @param projectPermissionsDTO The DTO containing project and user permission information.
     * @return A PermissionData object containing the permissions list, user list, and project, or null if the data could not be fetched.
     */
    private PermissionData fetchingRequiredData(ProjectPermissionsDTO projectPermissionsDTO) {
        if (projectPermissionsDTO.permissionDTO().isEmpty()) {
            return null;
        }

        List<Permission> permissionsList = permissionService.getPermissionsByPermissionType(projectPermissionsDTO.permissionDTO());
        if (permissionsList.isEmpty()) {
            logger.warn("The permissions are not present in the database.");
            return null;
        }

        List<Long> usersIds = projectPermissionsDTO.permissionDTO().stream()
                .map(PermissionDTO::userId)
                .toList();

        List<AppUser> userList = userService.getUsersByIds(usersIds);
        if (userList.isEmpty()) {
            return null;
        }

        Optional<Project> project = projectService.getProjectByProjectId(projectPermissionsDTO.projectId());
        return project.map(value -> new PermissionData(permissionsList, userList, value)).orElse(null);
    }

    private record PermissionData(List<Permission> permissionsList, List<AppUser> userList, Project project) {}
}