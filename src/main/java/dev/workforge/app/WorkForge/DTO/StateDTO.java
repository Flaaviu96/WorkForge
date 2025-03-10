package dev.workforge.app.WorkForge.DTO;

import dev.workforge.app.WorkForge.Model.StateType;
import lombok.Builder;

@Builder
public record StateDTO(
        long id,
        String name,
        StateType stateType
) {}
