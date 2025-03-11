package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
            "SELECT p FROM Project p " +
            "LEFT JOIN FETCH p.tasks t " +
            "LEFT JOIN FETCH t.comments c " +
            "WHERE p.id = :projectId"
    )
    Optional<Project> findTasksWithCommentsByProjectId(long projectId);

    @Query("SELECT p FROM Project p WHERE p.id NOT IN :projectIds")
    Page<Project> findAllProjectsExcludingIds(@Param("projectIds") List<Long> projectIds, Pageable pageable);
}
