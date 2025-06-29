package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Project;
import dev.workforge.app.WorkForge.Projections.ProjectProjection;
import dev.workforge.app.WorkForge.Projections.TaskProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
            "SELECT task.id AS taskId, " +
                    "task.taskName AS taskName, " +
                    "task.taskMetadata.assignedTo AS assignedTo, " +
                    "task.taskTimeTracking.remainingHours AS remainingHours, " +
                    "task.state.name AS stateName " +
                    "FROM Project p " +
                    "LEFT JOIN p.tasks task " +
                    "WHERE p.id = :projectId"
    )
    Optional<List<TaskProjection>> findTaskSummariesByProjectId(long projectId);

    @Query(
            "SELECT DISTINCT p from Project p "+
            "LEFT JOIN FETCH p.tasks "+
            "WHERE p.id = :projectId"
    )
    Optional<Project> findProjectWithTasks(long projectId);

    @Query(
            "SELECT p FROM Project p " +
                    "LEFT JOIN FETCH p.workflow w " +
                    "WHERE p.id = :projectId"
    )
    Optional<Project> findProjectWithWorkflow(long projectId);

    @Query("SELECT p FROM Project p WHERE p.id IN :projectsIds")
    List<Project> findProjectsByIds(List<Long> projectsIds);

    Optional<Project> findProjectByProjectKey(String projectKey);

    boolean existsByProjectName(String projectName);
}
