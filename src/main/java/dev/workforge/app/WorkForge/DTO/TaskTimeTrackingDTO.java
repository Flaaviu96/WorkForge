package dev.workforge.app.WorkForge.DTO;

public record TaskTimeTrackingDTO(
        double estimatedHours,
        double loggedHours,
        double remainingHours
) {}
