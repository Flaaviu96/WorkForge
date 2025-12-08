package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.model.UserPermission;
import dev.workforge.app.WorkForge.projections.UserPermissionProjection;
import dev.workforge.app.WorkForge.repository.UserPermissionRepository;
import dev.workforge.app.WorkForge.service.user_permission.UserPermissionRepositoryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserPermissionRepositoryServiceImpl implements UserPermissionRepositoryService {
    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionRepositoryServiceImpl(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @Override
    public void save(UserPermission userPermission) {
        userPermissionRepository.save(userPermission);
    }

    @Override
    public void saveAll(List<UserPermission> userPermissions) {
        userPermissionRepository.saveAll(userPermissions);
    }

    @Override
    public void delete(UserPermission userPermission) {

    }

    @Override
    public void deleteAll(List<UserPermission> userPermissions) {

    }

    @Override
    public List<UserPermission> findByUsersIdsAndProjectId(List<Long> userIds, Long projectId) {
        List<UserPermission> userPermissions = userPermissionRepository.findByUsersIdsAndProjectId(userIds, projectId);
        return userPermissions == null ? Collections.emptyList() : userPermissions;
    }

    @Override
    public List<UserPermissionProjection> findPermissionsByUser(String username) {
        List<UserPermissionProjection> userPermissionProjections = userPermissionRepository.findPermissionsByUser(username);
        return userPermissionProjections == null ? Collections.emptyList() : userPermissionProjections;
    }
}
