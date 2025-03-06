package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
}
