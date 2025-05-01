package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;
import java.util.List;
import java.util.Map;

@Builder
public record ProjectDTO(
        long id,
        String projectName,
        String projectDescription,
        List<TaskDTO> taskDTO,
        long workflowId,
        Map<StateDTO, List<StateDTO>> transitions
) {}
