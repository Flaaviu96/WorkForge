package dev.workforge.app.WorkForge;

import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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

    public AppInitializer(ProjectRepository projectRepository, UserRepository userRepository, TaskRepository taskRepository, StateRepository stateRepository, StateTransitionRepository stateTransitionRepository, WorkflowRepository workflowRepository, UserPermissionRepository userPermissionRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.stateRepository = stateRepository;
        this.stateTransitionRepository = stateTransitionRepository;
        this.workflowRepository = workflowRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        List<State> states = createStates();
        stateRepository.saveAllAndFlush(states);

        Workflow workflow = createWorkflow(states);

        createAndSaveProject(workflow);
        createAndSaveUser("dicas","dicas");
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

    private Workflow createWorkflow(List<State> states) {
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

    private void createAndSaveProject(Workflow workflow) {
        Project project = Project.builder()
                        .projectName("Test")
                                .workflow(workflow)
                                        .build();
        workflow.addProject(project);
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

        permissionRepository.saveAllAndFlush(List.of(readPermission, writePermission));

        UserPermission userPermission = new UserPermission();
        userPermission.setProject(project);
        userPermission.setUser(appUser.get());
        userPermission.addPermission(readPermission);

        userPermissionRepository.saveAndFlush(userPermission);
    }
}
