package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
}
