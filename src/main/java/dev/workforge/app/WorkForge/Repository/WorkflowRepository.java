package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    @Query(
            "SELECT w FROM Workflow w " +
            "JOIN FETCH w.stateTransitions st " +
            "JOIN FETCH st.fromState " +
            "JOIN FETCH st.toState " +
            "WHERE w.id = : workflowId "
    )
    Workflow findWorkflowWithStateTransitions(long workflowId);

}
