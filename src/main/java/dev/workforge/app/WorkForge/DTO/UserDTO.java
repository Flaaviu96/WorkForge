package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

@Builder
public record UserDTO(
        String username,
        String password
) {}
