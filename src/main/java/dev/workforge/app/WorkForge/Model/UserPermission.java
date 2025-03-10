package dev.workforge.app.WorkForge.Model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_permission", indexes = {
        @Index(name = "idx_user_project", columnList = "user_id, project_id")
})
@Data
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private List<Permission> permissions = new ArrayList<>();

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
}
