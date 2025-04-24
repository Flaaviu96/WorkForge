package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.StateDTO;
import dev.workforge.app.WorkForge.DTO.StateTransitionDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Enum.GlobalEnum;
import dev.workforge.app.WorkForge.Exceptions.ProjectNotFoundException;
import dev.workforge.app.WorkForge.Exceptions.ProjectUpdateFailedException;
import dev.workforge.app.WorkForge.Exceptions.TaskNotCreatedException;
import dev.workforge.app.WorkForge.Exceptions.WorkflowNotFoundException;
import dev.workforge.app.WorkForge.Mapper.ProjectMapper;
import dev.workforge.app.WorkForge.Mapper.StateTransitionMapper;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Repository.ProjectRepository;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.ProjectService;
import dev.workforge.app.WorkForge.Service.StateTransitionService;
import dev.workforge.app.WorkForge.Service.WorkflowService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final StateTransitionService stateTransitionService;
    private final WorkflowService workflowService;

    public ProjectServiceImpl(ProjectRepository projectRepository, StateTransitionService stateTransitionService, WorkflowService workflowService) {
        this.projectRepository = projectRepository;
        this.stateTransitionService = stateTransitionService;
        this.workflowService = workflowService;
    }

    @Override
    public List<TaskDTO> getTasksWithoutCommentsByProjectId(long projectId) {
        Project project = projectRepository.findTasksWithCommentsByProjectId(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "+projectId));
        return ProjectMapper.INSTANCE.toDTOWithTasks(project).taskDTO();
    }

    @Override
    public Optional<Project> getProjectByProjectId(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            return project;
        }
        throw new ProjectNotFoundException("Project not found with id: "+projectId);
    }

    @Override
    @PermissionCheck(permissionType = {PermissionType.READ}, parameter = "projectsIds")
    public List<ProjectDTO> getProjectsWithoutTasks(List<Long> projectsIds) {
        List<Project> projectList = projectRepository.findProjectsByIds(projectsIds);
        if (!projectList.isEmpty()) {
            return ProjectMapper.INSTANCE.toProjectsDTOWithoutTasks(projectList.stream().toList());
        }
        throw new ProjectNotFoundException("No projects found");
    }

    @Transactional
    @Override
    public TaskDTO saveNewTaskIntoProject(long projectId, TaskDTO taskDTO) {

        if (taskDTO != null && (taskDTO.taskName().isEmpty() || taskDTO.taskName().isBlank())) {
            throw new TaskNotCreatedException("The name of the task is empty or blank");
        }

        for (int counter = 0; counter < 3; counter++) {
            try {
                Optional<Project> optionalProject = projectRepository.findProjectWithTasks(projectId);
                if (optionalProject.isEmpty()) {
                    throw new ProjectNotFoundException("Project not found for the given ID.");
                }
                Project project = optionalProject.get();
                Task task = TaskMapper.INSTANCE.toTask(taskDTO);
                project.getTasks().add(task);
                task.setProject(optionalProject.get());
            } catch (OptimisticLockException e) {
                throw new ProjectUpdateFailedException("Project update failed after multiple attempts.");
            }
        }

        return taskDTO;
    }

    @Transactional
    @Override
    public ProjectDTO updateProjectPartially(long projectId, ProjectDTO projectDTO) {
        Optional<Project> optionalProject = projectRepository.findProjectWithWorkflow(projectId);

        if (optionalProject.isEmpty()) {
            throw new ProjectNotFoundException("Project not found for the given ID.");
        }

        Project project = optionalProject.get();

        if (projectDTO.projectName() != null && !projectDTO.projectName().isEmpty()) {
            project.setProjectName(projectDTO.projectName());
        }
        if (projectDTO.projectDescription() != null && !projectDTO.projectDescription().isEmpty()) {
            project.setProjectDescription(projectDTO.projectDescription());
        }
        if (projectDTO.workflowId() != 0 && project.getWorkflow().getId() != projectDTO.workflowId()) {
            Workflow workflow = workflowService.getWorkflowById(projectDTO.workflowId());
            project.setWorkflow(workflow);
            projectDTO.transitions().putAll(getWorkflowStateTransitionMap(workflow.getId()));
        }

        return ProjectMapper.INSTANCE.toDTOWithoutTasks(project);
    }

    @Override
    public ProjectDTO saveNewProject(ProjectDTO projectDTO) {

        if (projectDTO.projectName().isEmpty() || projectDTO.projectName().isBlank()) {
            throw new ProjectNotFoundException("The project is not valid");
        }

        boolean foundProjectName = projectRepository.hasProjectNameAlready(projectDTO.projectName());
        if (foundProjectName) {
            throw new ProjectNotFoundException("A project with this name exists already");
        }

        Project project = ProjectMapper.INSTANCE.toProjectWithoutTasks(projectDTO);
        Workflow workflow = workflowService.getWorkflowById(GlobalEnum.DEFAULT_WORKFLOW.getId());
        if (workflow == null) {
            throw new WorkflowNotFoundException("Default workflow not found");
        }

        project.setWorkflow(workflow);
        projectRepository.save(project);

        return ProjectMapper.INSTANCE.toDTOWithoutTasks(project);
    }

    private Map<StateDTO, List<StateDTO>> getWorkflowStateTransitionMap(long workflowId) {
        List<StateTransition>  stateTransitionList = stateTransitionService.getStatesTransitionsByWorkflowId(workflowId);
        List<StateTransitionDTO> stateTransitionDTOList = StateTransitionMapper.INSTANCE.toDTOWithList(stateTransitionList);
        return stateTransitionDTOList.stream()
                .collect(Collectors.groupingBy(StateTransitionDTO::fromStateDTO, Collectors.mapping(StateTransitionDTO::toStateDTO, Collectors.toList())));
    }
}
