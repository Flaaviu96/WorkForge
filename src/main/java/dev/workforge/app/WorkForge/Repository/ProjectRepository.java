package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Project;
import dev.workforge.app.WorkForge.Projections.ProjectProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
            "SELECT p.id AS projectId, p.projectName AS projectName, task AS tasks " +
            "FROM Project p " +
            "LEFT JOIN p.tasks task " +
            "WHERE p.id = :projectId"
    )
    Optional<ProjectProjection> findTasksWithCommentsByProjectId(long projectId);

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

    boolean existsByProjectName(String projectName);
    boolean existsByProjectKey(String projectKey);

}
