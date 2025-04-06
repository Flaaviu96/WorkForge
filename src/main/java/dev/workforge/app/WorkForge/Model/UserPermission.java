package dev.workforge.app.WorkForge.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_permission", indexes = {
        @Index(name = "idx_user_project", columnList = "user_id, project_id")
})
@Data
@Builder
@AllArgsConstructor
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public UserPermission() {

    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "user_permission_permissions",
            joinColumns = @JoinColumn(name = "user_permission_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void addPermissions(Set<Permission> permissions) {
        this.permissions.addAll(permissions);
    }

    public Set<Permission> getPermissions() {
        return new HashSet<>(permissions);
    }

    public void clearPermissions() {
        this.permissions.clear();
    }

    public void removePermisisons(Set<Permission> permissions) {
        this.permissions.removeAll(permissions);
    }
}
