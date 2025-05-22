package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;
import java.util.List;

@Builder
public record ProjectDTO(
        long id,
        String projectName,
        String projectDescription,
        List<TaskDTO> tasks,
        long workflowId
) {}
