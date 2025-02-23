package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p from Project p" +
            "JOIN FETCH p.tasks t"  +
            "LEFT JOIN FETCH t.comments c" +
            "WHERE p.id = :projectId")
    Optional<Project> findTasksWithCommentsByProjectId(long projectId);
}
