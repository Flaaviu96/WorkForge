package dev.workforge.app.WorkForge.service.user_permission;

import dev.workforge.app.WorkForge.model.UserPermission;
import dev.workforge.app.WorkForge.projections.UserPermissionProjection;

import java.util.List;

public interface UserPermissionRepositoryService {
    void save(UserPermission userPermission);
    void saveAll(List<UserPermission> userPermissions);
    void delete(UserPermission userPermission);
    void deleteAll(List<UserPermission> userPermissions);
    List<UserPermission> findByUsersIdsAndProjectId(List<Long> userIds, Long projectId);
    List<UserPermissionProjection> findPermissionsByUser(String username);
}
