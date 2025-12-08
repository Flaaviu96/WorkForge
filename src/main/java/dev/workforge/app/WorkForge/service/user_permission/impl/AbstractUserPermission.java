package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.dto.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.model.AppUser;
import dev.workforge.app.WorkForge.model.PermissionType;
import dev.workforge.app.WorkForge.model.UserPermission;
import dev.workforge.app.WorkForge.service.user_permission.UserPermissionRepositoryService;
import dev.workforge.app.WorkForge.service.usersession.UserPermissionCacheService;
import dev.workforge.app.WorkForge.service.usersession.impl.PermissionVersionService;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractUserPermission {

    protected final UserPermissionRepositoryService userPermissionRepositoryService;
    protected final UserPermissionDataFetcher userPermissionDataFetcher;
    protected final PermissionUtils permissionUtils;
    private final PermissionVersionService permissionVersionService;

    protected AbstractUserPermission(
            UserPermissionRepositoryService userPermissionRepositoryService,
            UserPermissionDataFetcher userPermissionDataFetcher,
            PermissionUtils permissionUtils, PermissionVersionService permissionVersionService
    ) {
        this.userPermissionRepositoryService = userPermissionRepositoryService;
        this.userPermissionDataFetcher = userPermissionDataFetcher;
        this.permissionUtils = permissionUtils;
        this.permissionVersionService = permissionVersionService;
    }

    @Transactional
    public void process(ProjectPermissionsDTO dto) {
        PermissionData data = userPermissionDataFetcher.fetchingRequiredData(dto);
        if (data == null) return;

        Map<Long, Set<PermissionType>> permissionsFromDTO =
                permissionUtils.groupPermissionsByUserIdFromDTO(dto.permissionDTO());

        List<UserPermission> existingPermissions =
                userPermissionRepositoryService.findByUsersIdsAndProjectId(
                        data.userList().stream().map(AppUser::getId).toList(),
                        data.project().getId()
                );

        Map<Long, UserPermission> existingMap = existingPermissions.stream()
                .collect(Collectors.toMap(up -> up.getUser().getId(), Function.identity()));
        handlePermissions(data, permissionsFromDTO, existingMap);
    }

    protected void updatePermissionSession(List<AppUser> appUsers, List<UserPermission> removing) {
        List<AppUser> appUsersUpdated = appUsers.stream()
                .filter(appUser -> removing.stream()
                        .anyMatch(userPermission -> userPermission.getUser().getId() == appUser.getId()))
                .toList();
        for (AppUser appUser : appUsersUpdated) {
            if (permissionVersionService.exists(String.valueOf(appUser.getId()))) {
                permissionVersionService.increment(String.valueOf(appUser.getId()));
            }
        }
    }

    protected abstract void handlePermissions(
            PermissionData data,
            Map<Long, Set<PermissionType>> permissionsFromDTO,
            Map<Long, UserPermission> existingPermissions
    );

    public void save(UserPermission userPermission) {
        userPermissionRepositoryService.save(userPermission);
    }

    public void saveAll(List<UserPermission> userPermissions) {
        userPermissionRepositoryService.saveAll(userPermissions);
    }

    public void deleteAll(List<UserPermission> userPermissions) {
        userPermissionRepositoryService.deleteAll(userPermissions);
    }
}
