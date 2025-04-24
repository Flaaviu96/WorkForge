package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_seq")
    @SequenceGenerator(name = "attachment_seq", sequenceName = "attachment_id_seq", allocationSize = 50)
    private Long id;

    private String fileName;

    private String fileType;

    private String path;

    private long size;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (projectId == null) {
            throw new IllegalStateException("projectId must not be null");
        }
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getPath() {
        return path;
    }
}
