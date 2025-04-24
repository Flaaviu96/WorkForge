package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query(
            "SELECT t FROM Task t " +
            "JOIN FETCH t.project " +
            "WHERE t.id = :taskId AND t.project.id = :projectId"
    )
    Task findTaskByIdAndProjectId(@Param("taskId") long taskId, @Param("projectId") long projectId);
}
