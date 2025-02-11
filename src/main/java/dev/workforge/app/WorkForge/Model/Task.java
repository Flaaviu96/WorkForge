package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_id_seq", allocationSize = 50)
    private long id;

    private String taskName;

    @OneToOne
    private State state;

    @OneToMany(mappedBy = "task",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attachment> attachments;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Embedded
    private TaskMetadata taskMetadata;

}
