package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

@Builder
public record TaskMetadataDTO(
     String assignedTo,
     String description
) {}
