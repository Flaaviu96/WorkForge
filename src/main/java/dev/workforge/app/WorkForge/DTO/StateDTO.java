package dev.workforge.app.WorkForge.DTO;

import dev.workforge.app.WorkForge.Model.StateType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;



@Builder
public record StateDTO(
        @Min(1)
        long id,

        @NotNull
        String name,

        @NotNull
        StateType stateType
) {}
