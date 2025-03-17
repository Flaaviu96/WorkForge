package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.Model.AppUser;
import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Repository.PermissionRepository;
import dev.workforge.app.WorkForge.Repository.UserPermissionProjection;
import dev.workforge.app.WorkForge.Repository.UserPermissionRepository;
import dev.workforge.app.WorkForge.Repository.UserRepository;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Service.PermissionService;
import dev.workforge.app.WorkForge.Service.ProjectService;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import dev.workforge.app.WorkForge.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final ProjectService projectService;

    public UserPermissionServiceImpl(UserPermissionRepository userPermissionRepository, PermissionService permissionService, UserService userService, ProjectService projectService) {
        this.userPermissionRepository = userPermissionRepository;
        this.permissionService = permissionService;
        this.userService = userService;
        this.projectService = projectService;
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
    @Transactional
    public void assignPermissionsForUsers(List<PermissionDTO> permissionDTOS) {
        if (permissionDTOS.isEmpty()) {
            return;
        }
        List<Permission> permissions = permissionService.getPermissionsByPermissionType(permissionDTOS);
        List<String> usernames = permissionDTOS.stream()
                .map(PermissionDTO::userName)
                .toList();
        List<AppUser> users = userService.getUsersByUsernames(usernames);
        List<Long> projectIds = permissionDTOS.stream()
                .map(PermissionDTO::projectId)
                .distinct()
                .toList();
        projectService.getProjectsByProjectIds(projectIds);
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
