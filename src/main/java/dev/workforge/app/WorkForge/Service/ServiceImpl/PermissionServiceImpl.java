package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.Exceptions.PermissionNotFoundException;
import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Repository.PermissionRepository;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Security.UserSessionService;
import dev.workforge.app.WorkForge.Service.PermissionService;
import dev.workforge.app.WorkForge.Service.SecurityUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserSessionService userSessionService;

    public PermissionServiceImpl(PermissionRepository permissionRepository, UserSessionService userSessionService) {
        this.permissionRepository = permissionRepository;
        this.userSessionService = userSessionService;
    }

    @Override
    public List<Permission> getPermissionsByPermissionType(List<PermissionDTO> permissionTypes) {
        if (permissionTypes.isEmpty()) {
            return Collections.emptyList();
        }
        List<PermissionType> permissionTypeList = permissionTypes.stream()
                .map(this::mapPermissionDTO)
                .distinct()
                .toList();
        List<Permission> permissions = permissionRepository.findPermissionByPermissionType(permissionTypeList);
        if (permissions.isEmpty()) {
            throw new PermissionNotFoundException("The permission is not found");
        }
        return permissions;
    }

    private PermissionType mapPermissionDTO(PermissionDTO permissionDTO) {
        return permissionDTO.permissionType();
    }

    private SecurityUser retrieveSecurityUser() {
        return (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean hasRequiredPermissions(PermissionType[] permissionTypes, Map<Long, Set<Permission>> permissions, long projectId) {
        Set<Permission> permissionSet = permissions.get(projectId);
        if (permissionSet == null) {
            return false;
        }

        if (hasWriteWithoutRead(permissionSet)) {
            return false;
        }
        for (PermissionType permissionType : permissionTypes) {
            if (permissionSet.stream().noneMatch(permission -> permission.getPermissionType() == permissionType)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasWriteWithoutRead(Set<Permission> permissions) {
        boolean hasWrite = permissions.stream().anyMatch(permission -> permission.getPermissionType() == PermissionType.WRITE);
        boolean hasRead = permissions.stream().anyMatch(permission -> permission.getPermissionType() == PermissionType.READ);

        return hasWrite && !hasRead;
    }
    private boolean hasPermissionsChanged(String sessionId) {
        long lastPermissionsUpdateFromRedis = userSessionService.getPermissionFromRedis(String.valueOf(retrieveSecurityUser().getId()));
        long lastPermissionsUpdateFromContext = retrieveSecurityUser().getLastPermissionsUpdate();
        return lastPermissionsUpdateFromRedis > lastPermissionsUpdateFromContext;
    }
}
