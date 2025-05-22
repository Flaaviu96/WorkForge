package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Model.UserPermission;
import dev.workforge.app.WorkForge.Projections.UserPermissionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    @Query(
            "SELECT u.project.id AS projectId, permission AS permissions " +
            "FROM UserPermission u " +
            "JOIN u.permissions permission " +
            "WHERE u.user.username = :username"
    )
    List<UserPermissionProjection> findPermissionsByUser(@Param("username") String username);

    @Query(
            "SELECT COUNT(u) > 0 FROM UserPermission u " +
            "JOIN u.permissions permission " +
            "WHERE u.id = :id AND permission.permissionType = :permissionType"
    )
    boolean isPermissionAssignedForUser (@Param("id") long id, @Param("permissionType") PermissionType permissionType);

    @Query(
            "SELECT u FROM UserPermission u " +
            "JOIN u.project project " +
            "WHERE u.user.id IN :ids AND u.project.id = :projectId"
    )
    List<UserPermission> findByUsersIdsAndProjectId(@Param("ids") List<Long> usersIds, @Param("projectId") long projectId);
}
