package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_id_seq", allocationSize = 50)
    private long id;

    private String projectName;

    private String projectDescription;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    public Set<Task> getTasks() {
        return tasks;
    }
}
