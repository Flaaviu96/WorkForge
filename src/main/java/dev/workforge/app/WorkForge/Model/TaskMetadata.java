package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class TaskMetadata {

    private String assignedTo;
    private String createdBy;
    private String description;

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    public void setCreatedBy(String createdBy) {
        if (description != null) {
            this.createdBy = createdBy;
        }
    }

    public void setAssignedTo(String assignedTo) {
        if (assignedTo != null) {
            this.assignedTo = assignedTo;
        }
    }
}
