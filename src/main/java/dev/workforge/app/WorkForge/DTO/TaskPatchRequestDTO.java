package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

import java.util.Date;

@Builder
public record TaskPatchRequestDTO(
        String taskName,
        String fromState,
        String toState,
        Date modifiedDate,
        TaskMetadataDTO taskMetadataDTO,
        TaskTimeTrackingDTO taskTimeTrackingDTO,
        String userUUID
) {}
