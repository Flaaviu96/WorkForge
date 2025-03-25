package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record ProjectPermissionsDTO(
        long projectId,
        List<PermissionDTO> permissionDTO
) {}
