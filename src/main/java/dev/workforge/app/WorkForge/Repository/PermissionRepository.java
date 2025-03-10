package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
