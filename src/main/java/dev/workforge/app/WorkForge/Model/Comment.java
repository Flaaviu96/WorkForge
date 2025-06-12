package dev.workforge.app.WorkForge.Model;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @SequenceGenerator(name = "comment_seq", sequenceName = "comment_id_seq", allocationSize = 50)
    private long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(nullable = false)
    long projectId;

    private String author;

    @Column(length = 250)
    private String content;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date modifiedDate;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (projectId == 0) {
            throw new IllegalStateException("projectId must not be 0");
        }
    }

    public Task getTask() {
        return task;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", task=" + task +
                ", projectId=" + projectId +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
}
