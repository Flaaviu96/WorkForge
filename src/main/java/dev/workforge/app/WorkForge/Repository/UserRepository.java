package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    @Query(
            "SELECT u FROM AppUser u " +
            "WHERE u.username = :username"
    )
    AppUser findByUsername(String username);
}
