package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StateTransition> stateTransitions = new HashSet<>();

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();

    public void addStateTransition(StateTransition stateTransition) {
        stateTransitions.add(stateTransition);
        stateTransition.setWorkflow(this);
    }

    public void removeStateTransition(StateTransition stateTransition) {
        stateTransitions.remove(stateTransition);
        stateTransition.setWorkflow(null);
    }

    public void addProject(Project project) {
        projects.add(project);
        project.setWorkflow(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.setWorkflow(null);
    }
}
