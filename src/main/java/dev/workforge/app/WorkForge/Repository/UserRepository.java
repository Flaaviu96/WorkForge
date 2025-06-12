package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    @Query(
            "SELECT u FROM AppUser u " +
            "WHERE u.username = :username"
    )
    Optional<AppUser> findByUsername(String username);

    @Query(
            "SELECT u FROM AppUser u " +
            "WHERE u.username IN :usersIds"
    )
    List<AppUser> findUsersByIds(List<Long> usersIds);

    @Query(
            "SELECT u FROM AppUser u " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT(:prefix, '%'))"
    )
    List<AppUser> findUsersByPrefix(String prefix);

    @Query(
            "SELECT u FROM AppUser u " +
                    "WHERE u.uuid = :uuid"
    )
    AppUser userExists(@Param("uuid") UUID uuid);
}
