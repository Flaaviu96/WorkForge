package dev.workforge.app.WorkForge.DTO;

import java.util.Date;

public record TaskPatchResponseDTO(
        String taskName,
        String state,
        Date modifiedDate,
        TaskMetadataDTO taskMetadataDTO
) {}
