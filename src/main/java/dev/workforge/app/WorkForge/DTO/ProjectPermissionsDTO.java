package dev.workforge.app.WorkForge.DTO;

import java.util.List;

public record ProjectPermissionsDTO(
        long projectId,
        List<PermissionDTO> permissionDTO
) {}
