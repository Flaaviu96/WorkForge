package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

@Builder
public record CreateProjectDTO (
        String projectName,
        String projectDescription,
        String projectOwner
) {}

