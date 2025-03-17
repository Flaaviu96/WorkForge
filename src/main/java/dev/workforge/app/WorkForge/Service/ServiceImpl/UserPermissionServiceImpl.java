package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Repository.UserPermissionProjection;
import dev.workforge.app.WorkForge.Repository.UserPermissionRepository;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionServiceImpl(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @Override
    public void loadUserPermissions(UserDetails userDetails) {
        List<UserPermissionProjection> userPermission = userPermissionRepository.findPermissionsByUser(userDetails.getUsername());
        addPermissionsToUser(userDetails, userPermission, false);
    }

    @Override
    public List<Long> extractProjectIdsFromSecurityContext() {
        Map<Long, Set<Permission>> permissions = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPermissionMap();
        return permissions.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(permission -> permission.getPermissionType() == PermissionType.READ))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public void refreshUserPermissions (UserDetails userDetails) {
        List<UserPermissionProjection> userPermission = userPermissionRepository.findPermissionsByUser(userDetails.getUsername());
        addPermissionsToUser(userDetails, userPermission, true);
    }

    @Override
    public void assignPermissionsForUsers(List<PermissionDTO> permissionDTOS) {
        Set<PermissionType> permissionTypes = permissionDTOS.stream()
                .map(PermissionDTO::permissionType)
                .collect(Collectors.toSet());

    }

    private void addPermissionsToUser(UserDetails userDetails, List<UserPermissionProjection> userPermissionList, boolean updatePermissions) {
        if (updatePermissions) {
            ((SecurityUser) userDetails).clearMap();
        }

        for (UserPermissionProjection userPermission : userPermissionList) {
            ((SecurityUser) userDetails).addPermissions(userPermission.getProjectId(), userPermission.getPermissions());
        }
    }

    private Map<Long, Set<Permission>> transformToMap(List<UserPermissionProjection> list) {
        Map<Long, Set<Permission>> map = new HashMap<>();
        for (UserPermissionProjection userPermissionProjection : list) {
            map.computeIfAbsent(userPermissionProjection.getProjectId(), k -> new HashSet<>(userPermissionProjection.getPermissions()));
        }
        return map;
    }
}
