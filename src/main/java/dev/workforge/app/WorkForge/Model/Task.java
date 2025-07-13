package dev.workforge.app.WorkForge.Model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
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

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<Attachment> attachments = new HashSet<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Embedded
    private TaskMetadata taskMetadata;

    @Embedded
    private TaskTimeTracking taskTimeTracking;

    @Version
    private Integer version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date modifiedDate;

    public long getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public State getState() {
        return state;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Project getProject() {
        return project;
    }

    public TaskMetadata getTaskMetadata() {
        return taskMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(state, task.state) && Objects.equals(project, task.project) && Objects.equals(taskMetadata, task.taskMetadata) && Objects.equals(version, task.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, state, project, taskMetadata, version);
    }

}
