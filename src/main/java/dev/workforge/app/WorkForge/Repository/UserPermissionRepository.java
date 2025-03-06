package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    @Query(
            "SELECT u.project.projectName AS projectKey, u.permission AS permission " +
                    "FROM UserPermission u " +
                    "WHERE u.user.username = :username"
    )
    List<UserPermissionProjection> findPermissionsByUser(@Param("username") String username);
}
