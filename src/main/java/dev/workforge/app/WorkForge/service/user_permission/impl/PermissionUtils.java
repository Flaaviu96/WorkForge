package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.dto.PermissionDTO;
import dev.workforge.app.WorkForge.model.Permission;
import dev.workforge.app.WorkForge.model.PermissionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionUtils {
    /**
     * Helper method that groups permissions by user ID from the provided list of PermissionDTO objects.
     *
     * @param permissionDTO List of PermissionDTO objects containing permission information.
     * @return A map where the key is the user ID and the value is a set of permission types assigned to that user.
     */
    public Map<Long, Set<PermissionType>> groupPermissionsByUserIdFromDTO(List<PermissionDTO> permissionDTO) {
        return permissionDTO.stream()
                .collect(Collectors.groupingBy(
                        PermissionDTO::userId,
                        Collectors.mapping(PermissionDTO::permissionType, Collectors.toSet())
                ));
    }

    public Set<Permission> getPermissionsByPermissionTypes(List<Permission> permissionsList, Set<PermissionType> permissionTypes) {
        return permissionsList.stream()
                .filter(permission -> permissionTypes.contains(permission.getPermissionType()))
                .collect(Collectors.toSet());
    }
}
