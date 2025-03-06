package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
}
