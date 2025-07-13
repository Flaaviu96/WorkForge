package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

import java.util.Date;

@Builder
public record TaskPatchDTO(
        String taskName,
        String state,
        Date modifiedDate,
        TaskMetadataDTO taskMetadataDTO,
        TaskTimeTrackingDTO taskTimeTrackingDTO
) {}
