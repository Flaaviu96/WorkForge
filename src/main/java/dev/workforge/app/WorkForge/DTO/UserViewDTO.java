package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserViewDTO(
        UUID uuid,
        String username
) {}
