package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;

import java.util.List;

public interface PermissionService {
    List<Permission> getPermissionsByDTO(List<PermissionDTO> permissionDTOS);

    List<Permission> getPermissionsByPermissionType(List<PermissionType> types);
}
