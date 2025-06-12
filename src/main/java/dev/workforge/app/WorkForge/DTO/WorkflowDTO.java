package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;
import java.util.List;
import java.util.Map;

@Builder
public record WorkflowDTO (
        long projectId,
        Map<StateDTO, List<StateDTO>> stateDTOListMap
) {}
