package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_id_seq", allocationSize = 50)
    private long id;

    private String projectName;

    private String projectDescription;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", orphanRemoval = true, cascade = CascadeType.ALL)
    Set<Task> taskSet = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

}
