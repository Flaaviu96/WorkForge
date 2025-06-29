package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class TaskTimeTracking {

    private double estimatedHours;
    private double loggedHours;
    private double remainingHours;

    public TaskTimeTracking() {
        this.estimatedHours = 0;
        this.loggedHours = 0;
        this.remainingHours = 0;
    }

    public double getEstimatedHours() {
        return estimatedHours;
    }

    public double getLoggedHours() {
        return loggedHours;
    }

    public double getRemainingHours() {
        return remainingHours;
    }
}
