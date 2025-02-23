package dev.workforge.app.WorkForge.Model;


import jakarta.persistence.*;

@Entity
@Table(name = "user_permission", indexes = {
        @Index(name = "idx_user_project", columnList = "user_id, project_id"),
        @Index(name = "idx_permission", columnList = "permission")
})
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permission permission;
}
