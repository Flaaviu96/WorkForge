package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

import java.util.Set;

@Builder
public record ProjectDTO(
        long id,
        String projectName,
        String projectDescription,
        Set<TaskDTO> taskDTO,
        WorkflowDTO workflowDTO
) {}
