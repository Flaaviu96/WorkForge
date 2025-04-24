package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
            "SELECT p FROM Project p " +
            "LEFT JOIN FETCH p.tasks t " +
            "WHERE p.id = :projectId"
    )
    Optional<Project> findTasksWithCommentsByProjectId(long projectId);

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

    @Query(
            "SELECT CASE WHEN COUNT(p.projectName) > 0 THEN true ELSE false END " +
                    "FROM Project p " +
                    "WHERE p.projectName = :projectName"
    )
    boolean hasProjectNameAlready(String projectName);
}
