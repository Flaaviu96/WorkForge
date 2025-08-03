package dev.workforge.app.WorkForge.DTO;

import java.util.Date;

public record TaskSummaryDTO(
        long taskId,
        String taskName,
        String state,
        Date createdDate,
        String assignedTo
) {}
