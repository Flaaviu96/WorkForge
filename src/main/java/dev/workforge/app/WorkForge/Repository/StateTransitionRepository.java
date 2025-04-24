package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.StateTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StateTransitionRepository extends JpaRepository<StateTransition, Long> {

    @Query("SELECT st FROM StateTransition st JOIN FETCH st.workflow WHERE st.workflow.id = :workflowId")
    List<StateTransition> findAllStateTransitionByWorkflowId(@Param("workflowId") long workflowId);
}
