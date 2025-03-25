package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "permissions")
@Data
@Builder(builderClassName = "PermissionBuilder")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PermissionType permissionType;

    private String description;

    public Permission setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id) && permissionType == that.permissionType && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permissionType, description);
    }
}
