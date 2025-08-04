package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;

import java.util.List;

public interface PermissionService {

    /**
     * Retrieves a list of Permission entities that correspond to the given list of PermissionDTOs.
     * This method maps or converts the DTO representations into their full Permission model objects.
     *
     * @param permissionDTOS the list of PermissionDTO objects to be converted to Permission entities
     * @return a list of Permission entities matching the provided DTOs
     */
    List<Permission> getPermissionsByDTO(List<PermissionDTO> permissionDTOS);

    /**
     * Fetches all Permission entities that match any of the provided PermissionType values.
     *
     * @param types the list of PermissionType enums to filter permissions by
     * @return a list of Permission entities with the specified types
     */
    List<Permission> getPermissionsByPermissionType(List<PermissionType> types);
}
