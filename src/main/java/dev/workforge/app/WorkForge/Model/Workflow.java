package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workflow_seq")
    @SequenceGenerator(name = "workflow_seq", sequenceName = "workflow_id_seq", allocationSize = 50)
    private long id;

    private String workflowName;

    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflow", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<StateTransition> stateTransitions;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> projects;
}
