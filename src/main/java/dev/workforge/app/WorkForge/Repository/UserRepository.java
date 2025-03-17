package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    @Query(
            "SELECT u FROM AppUser u " +
            "WHERE u.username = :username"
    )
    Optional<AppUser> findByUsername(String username);

    @Query(
            "SELECT u FROM AppUser u " +
            "WHERE u.username IN :usernames"
    )
    List<AppUser> findUsersByUsername(@Param("usernames") List<String> usernames);
}
