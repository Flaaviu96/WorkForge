package dev.workforge.app.WorkForge.Model;

import dev.workforge.app.WorkForge.Util.ProjectKeyGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Builder
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_id_seq", allocationSize = 50)
    private long id;

    @Version
    private int version;

    private String projectName;

    private String projectDescription;

    @Column(nullable = false, unique = true, updatable = false)
    private String projectKey;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    public Project() {

    }

    public void addTasks(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }

    public long getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public String getProjectKey() {
        return projectKey;
    }
}
