package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
            "JOIN FETCH u.project p " +
            "WHERE u.user.id IN :ids"
    )
    List<UserPermission> findByUserIds(@Param("ids") List<Long> usersIds);
}
