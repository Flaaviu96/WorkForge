package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;
import java.util.Set;

@Builder
public record WorkflowDTO (
     long id,
     String workflowName,
     String description,
     Set<StateTransitionDTO> stateTransitionDTOS,
     Set<ProjectDTO> projectDTOS
) {}
