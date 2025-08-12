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

import java.util.HashSet;
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

        createAndSaveProject(workflow, states);
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

        workflowRepository.saveAndFlush(workflow);
        return workflow;
    }

    private Task createTask(Project project, State state, String taskName) {
        Task task = new Task();
        task.setTaskName("Test");
        task.setTaskName(taskName);
        task.setProject(project);
        task.setState(state);
        return task;
    }

    private Comment createComment(Task task, String content, String author, long projectId) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setProjectId(projectId);
        comment.setTask(task);
        return comment;
    }

    private void createAndSaveProject(Workflow workflow, List<State> states) {
        Project project = Project.builder()
                        .projectName("Test")
                                .workflow(workflow)
                .projectKey(ProjectKeyGenerator.generateKey("Test"))
                                        .build();

        List<String> taskNames = List.of(
                "backend-integration", "ui-polish", "api-error-handling", "documentation", "unit-tests",
                "performance-optimization", "database-migration", "email-service", "websocket-setup", "bugfix-login",
                "refactor-service-layer", "security-patch", "frontend-pagination", "image-upload", "report-generation",
                "user-profile-edit", "dark-mode-support", "data-export", "push-notifications", "analytics-dashboard"
        );

        List<String> descriptions = List.of(
                "Implement backend integration for authentication",
                "Polish UI with Tailwind and accessibility fixes",
                "Improve API error handling",
                "Write developer documentation",
                "Add unit tests for service layer",
                "Optimize query performance",
                "Migrate database to new schema",
                "Implement email notification service",
                "Setup WebSocket communication",
                "Fix login redirect bug",
                "Refactor service layer for maintainability",
                "Apply latest security patch",
                "Implement frontend pagination",
                "Add image upload feature",
                "Generate PDF reports",
                "Allow users to edit profile info",
                "Add dark mode toggle",
                "Export data in CSV format",
                "Implement push notifications",
                "Create analytics dashboard"
        );

        List<String> assignees = List.of("Alice", "Bob", "Charlie", "Dicas", "Eve");

        Set<Task> taskSet = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            Task taskX = createTask(project, states.get(i), taskNames.get(i));

            TaskMetadata metadata = new TaskMetadata();
            metadata.setDescription(descriptions.get(i));
            metadata.setAssignedTo(assignees.get(i % assignees.size())); // cycle through assignees
            metadata.setCreatedBy("system");
            taskX.setTaskMetadata(metadata);

            taskX.setTaskTimeTracking(new TaskTimeTracking());

            // Add 1–2 comments
            taskX.getComments().add(createComment(taskX, "Initial setup for " + taskNames.get(i), metadata.getCreatedBy(), 1));
            if (i % 2 == 0) {
                taskX.getComments().add(createComment(taskX, "Follow-up for " + taskNames.get(i), metadata.getAssignedTo(), 2));
            }

            taskSet.add(taskX);
        }

        project.setTasks(taskSet);



        projectRepository.saveAndFlush(project);
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
