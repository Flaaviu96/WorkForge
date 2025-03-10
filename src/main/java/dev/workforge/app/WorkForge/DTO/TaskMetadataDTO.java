package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

import java.util.Date;

@Builder
public record TaskMetadataDTO(
     String assignedTo,
     String createdBy,
     String description,
     Date createdDate,
     Date modifiedDate
) {}
