package dev.workforge.app.WorkForge;

import dev.workforge.app.WorkForge.Model.State;
import dev.workforge.app.WorkForge.Model.StateType;
import dev.workforge.app.WorkForge.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppInitializer implements CommandLineRunner {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final StateRepository stateRepository;
    private final StateTransitionRepository stateTransitionRepository;

    public AppInitializer(ProjectRepository projectRepository, UserRepository userRepository, TaskRepository taskRepository, StateRepository stateRepository, StateTransitionRepository stateTransitionRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.stateRepository = stateRepository;
        this.stateTransitionRepository = stateTransitionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("START");
        State start = new State();
        start.setName("START");
        start.setStateType(StateType.INITIAL);

        State progress = new State();
        progress.setName("PROGRESS");
        progress.setStateType(StateType.INTERMEDIATE);

        State end = new State();
        end.setName("END");
        end.setStateType(StateType.FINAL);

        stateRepository.saveAllAndFlush(List.of(start,progress, end));
        System.out.println("Done");
    }
}
