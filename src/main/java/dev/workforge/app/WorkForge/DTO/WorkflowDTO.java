package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;
import java.util.List;

@Builder
public record WorkflowDTO (
     long id,
     String workflowName,
     String description,
     List<StateTransitionDTO> stateTransitionDTOS,
     List<ProjectDTO> projectDTOS
) {}
