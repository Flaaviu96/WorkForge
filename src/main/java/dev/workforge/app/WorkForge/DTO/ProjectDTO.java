package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;
import java.util.List;

@Builder
public record ProjectDTO(
        String id,
        String projectKey,
        String projectName,
        String projectDescription,
        List<TaskDTO> tasks,
        long workflowId
) {}
