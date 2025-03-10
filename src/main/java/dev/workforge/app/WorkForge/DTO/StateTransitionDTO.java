package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

@Builder
public record StateTransitionDTO(
        long id,
        StateDTO fromStateDTO,
        StateDTO toStateDTO,
        WorkflowDTO workflowDTO
) {}
