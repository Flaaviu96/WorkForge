package dev.workforge.app.WorkForge.DTO;

import dev.workforge.app.WorkForge.Model.PermissionType;
import lombok.Builder;

@Builder
public record PermissionDTO(
        long userId,
        PermissionType permissionType
) {}
