package dev.workforge.app.WorkForge;

import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Repository.*;
import dev.workforge.app.WorkForge.Trigger.AbstractTrigger;
import dev.workforge.app.WorkForge.Trigger.TriggerSendEmail;
import dev.workforge.app.WorkForge.Util.ProjectKeyGenerator;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class AppInitializer implements CommandLineRunner {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final StateRepository stateRepository;
    private final StateTransitionRepository stateTransitionRepository;
    private final WorkflowRepository workflowRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AbstractTriggerRepository abstractTriggerRepository;

    public AppInitializer(ProjectRepository projectRepository, UserRepository userRepository, TaskRepository taskRepository, StateRepository stateRepository, StateTransitionRepository stateTransitionRepository, WorkflowRepository workflowRepository, UserPermissionRepository userPermissionRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder, AbstractTriggerRepository abstractTriggerRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.stateRepository = stateRepository;
        this.stateTransitionRepository = stateTransitionRepository;
        this.workflowRepository = workflowRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.abstractTriggerRepository = abstractTriggerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<State> states = createStates();
        stateRepository.saveAllAndFlush(states);

        Workflow workflow = createWorkflow(states, createAbstractTrigger());

        createAndSaveProject(workflow, states.get(0));
        createAndSaveUser("dicas","dicas");
        createAndSaveUser("test","test");
        createAndSaveUserPermissions("dicas",1L);
    }

    private List<State> createStates() {
        State start = new State();
        start.setName("START");
        start.setStateType(StateType.INITIAL);

        State progress = new State();
        progress.setName("PROGRESS");
        progress.setStateType(StateType.INTERMEDIATE);

        State onHold = new State();
        onHold.setName("ON HOLD");
        onHold.setStateType(StateType.INTERMEDIATE);

        State end = new State();
        end.setName("END");
        end.setStateType(StateType.FINAL);

        return List.of(start, progress, onHold, end);
    }

    private AbstractTrigger createAbstractTrigger() {
        AbstractTrigger abstractTrigger = new TriggerSendEmail();
        return abstractTriggerRepository.save(abstractTrigger);
    }

    private Workflow createWorkflow(List<State> states, AbstractTrigger abstractTrigger) {
        Workflow workflow = new Workflow();
        workflow.setDescription("This is a description");
        workflow.setWorkflowName("Default workflow");

        // Create state transitions
        StateTransition startToProgress = new StateTransition();
        startToProgress.setFromState(states.get(0));  // START
        startToProgress.setToState(states.get(1));    // PROGRESS

        StateTransition progressToEnd = new StateTransition();
        progressToEnd.setFromState(states.get(1));    // PROGRESS
        progressToEnd.setToState(states.get(3));      // END

        workflow.addStateTransition(startToProgress);
        workflow.addStateTransition(progressToEnd);

        return workflow;
    }

    private Task createTask(Project project, State state, String taskName, List<Comment> comments) {
        Task task = new Task();
        task.setTaskName("Test");
        task.setTaskName(taskName);
        task.setProject(project);
        task.setState(state);
        if (comments != null) {
            for (Comment comment : comments) {
                task.getComments().add(comment);
                comment.setTask(task);
            }
        }
        return task;
    }

    private Comment createComment(String content, String author, long projectId) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setProjectId(projectId);
        return comment;
    }

    private void createAndSaveProject(Workflow workflow, State state) {
        Project project = Project.builder()
                        .projectName("Test")
                                .workflow(workflow)
                .projectKey(ProjectKeyGenerator.generateKey("Test"))
                                        .build();
        workflow.addProject(project);
        Comment comment = createComment("This is a test for the frontend", "dicas", 1);
        Comment comment1 = createComment("This is another test", "dicas", 1);
        Task task = createTask(project, state, "test1", List.of(comment, comment1));
        TaskMetadata taskMetadata = new TaskMetadata();
        taskMetadata.setDescription("This is a dummy description for the task");
        taskMetadata.setAssignedTo("Dicas");
        taskMetadata.setCreatedBy("dicas");
        task.setTaskMetadata(taskMetadata);
        TaskTimeTracking taskTimeTracking = new TaskTimeTracking();
        task.setTaskTimeTracking(taskTimeTracking);

        Task task1 = createTask(project, state, "te", List.of(comment, comment1));
        Task task2 = createTask(project, state, "tes", List.of(comment, comment1));
        Task task3 = createTask(project, state, "tesa", List.of(comment, comment1));
        Task task4 = createTask(project, state, "task4", List.of(comment, comment1));
        Task task5 = createTask(project, state, "task5", List.of(comment, comment1));
        Task task6 = createTask(project, state, "task6", List.of(comment, comment1));
        Task task7 = createTask(project, state, "task7", List.of(comment, comment1));
        Task task8 = createTask(project, state, "task8", List.of(comment, comment1));
        Task task9 = createTask(project, state, "task9", List.of(comment, comment1));
        Task task10 = createTask(project, state, "task10", List.of(comment, comment1));
        Task task11 = createTask(project, state, "task11", List.of(comment, comment1));
        Task task12 = createTask(project, state, "task12", List.of(comment, comment1));
        Task task13 = createTask(project, state, "task13", List.of(comment, comment1));
        Task task14 = createTask(project, state, "task14", List.of(comment, comment1));
        Task task15 = createTask(project, state, "task15", List.of(comment, comment1));
        Task task16 = createTask(project, state, "task16", List.of(comment, comment1));

        project.setTasks(Set.of(
                task1, task2, task3, task4, task5, task6, task7, task8,
                task9, task10, task11, task12, task13, task14, task15, task16
        ));

        workflowRepository.saveAndFlush(workflow);
    }

    private void createAndSaveUser(String username, String password) {
        AppUser appUser = AppUser.builder()
                        .username(username)
                                .password(passwordEncoder.encode(password))
                                        .build();
        userRepository.saveAndFlush(appUser);
    }

    private void createAndSaveUserPermissions(String username, long id) {
        Optional<AppUser> appUser = userRepository.findByUsername(username);
        Project project = projectRepository.findById(id).orElseThrow();

        Permission readPermission = Permission.builder()
                .permissionType(PermissionType.READ)
                .description("Read right")
                .build();

        Permission writePermission = Permission.builder()
                .permissionType(PermissionType.WRITE)
                .description("Write right")
                .build();

        Permission projectAdmin = Permission.builder()
                .permissionType(PermissionType.PROJECT_ADMIN)
                .description("Write right")
                .build();

        permissionRepository.saveAllAndFlush(List.of(readPermission, writePermission, projectAdmin));

        UserPermission userPermission = new UserPermission();
        userPermission.setProject(project);
        userPermission.setUser(appUser.get());
        userPermission.addPermission(readPermission);
        userPermission.addPermission(writePermission);

        userPermissionRepository.saveAndFlush(userPermission);
    }
}
