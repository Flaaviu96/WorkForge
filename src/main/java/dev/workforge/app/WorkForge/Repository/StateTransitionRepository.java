package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.StateTransition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateTransitionRepository extends JpaRepository<StateTransition, Long> {
}
