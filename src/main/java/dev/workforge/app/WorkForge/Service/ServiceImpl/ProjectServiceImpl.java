package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.StateDTO;
import dev.workforge.app.WorkForge.DTO.StateTransitionDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Enum.GlobalEnum;
import dev.workforge.app.WorkForge.Exceptions.ProjectException;
import dev.workforge.app.WorkForge.Exceptions.TaskException;
import dev.workforge.app.WorkForge.Exceptions.WorkflowException;
import dev.workforge.app.WorkForge.Mapper.ProjectMapper;
import dev.workforge.app.WorkForge.Mapper.StateTransitionMapper;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Projections.ProjectProjection;
import dev.workforge.app.WorkForge.Repository.ProjectRepository;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.ProjectService;
import dev.workforge.app.WorkForge.Service.StateTransitionService;
import dev.workforge.app.WorkForge.Service.WorkflowService;
import dev.workforge.app.WorkForge.Util.ErrorMessages;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
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
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, StateTransitionService stateTransitionService, WorkflowService workflowService, ProjectMapper projectMapper, TaskMapper taskMapper) {
        this.projectRepository = projectRepository;
        this.stateTransitionService = stateTransitionService;
        this.workflowService = workflowService;
        this.projectMapper = projectMapper;
        this.taskMapper = taskMapper;
    }

    @Override
    public ProjectDTO getTasksWithoutCommentsByProjectId(long projectId) {
        ProjectProjection project = projectRepository.findTasksWithCommentsByProjectId(projectId)
                .orElseThrow(() -> new ProjectException(ErrorMessages.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND));
        return projectMapper.toDTOWithTasks(project);
    }

    @Override
    public Optional<Project> getProjectByProjectId(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            return project;
        }
        throw new ProjectException(ErrorMessages.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @Override
    @PermissionCheck(permissionType = {PermissionType.READ}, parameter = "projectsIds")
    public List<ProjectDTO> getProjectsWithoutTasks(List<Long> projectsIds) {
        List<Project> projectList = projectRepository.findProjectsByIds(projectsIds);
        if (!projectList.isEmpty()) {
            return projectMapper.toProjectsDTOWithoutTasks(projectList.stream().toList());
        }
        throw new ProjectException(ErrorMessages.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @Transactional
    @Override
    public TaskDTO saveNewTaskIntoProject(long projectId, TaskDTO taskDTO) {

        if (taskDTO != null && (taskDTO.taskName().isEmpty() || taskDTO.taskName().isBlank())) {
            throw new TaskException(ErrorMessages.INVALID_ID, HttpStatus.BAD_REQUEST);
        }

        for (int counter = 0; counter < 3; counter++) {
            try {
                Optional<Project> optionalProject = projectRepository.findProjectWithTasks(projectId);
                if (optionalProject.isEmpty()) {
                    throw new ProjectException(ErrorMessages.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                Project project = optionalProject.get();
                Task task = taskMapper.toTask(taskDTO);
                project.getTasks().add(task);
                task.setProject(optionalProject.get());
            } catch (OptimisticLockException e) {
                throw new ProjectException(ErrorMessages.PROJECT_UPDATE_FAILED, HttpStatus.BAD_REQUEST);
            }
        }

        return taskDTO;
    }

    @Transactional
    @Override
    public ProjectDTO updateProjectPartially(long projectId, ProjectDTO projectDTO) {
        Optional<Project> optionalProject = projectRepository.findProjectWithWorkflow(projectId);

        if (optionalProject.isEmpty()) {
            throw new ProjectException(ErrorMessages.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
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
          //  projectDTO.transitions().putAll(getWorkflowStateTransitionMap(workflow.getId()));
        }

        return projectMapper.toDTOWithoutTasks(project);
    }

    @Override
    public ProjectDTO saveNewProject(ProjectDTO projectDTO) {

        if (projectDTO.projectName().isEmpty() || projectDTO.projectName().isBlank()) {
            throw new ProjectException(ErrorMessages.INVALID_ID, HttpStatus.BAD_REQUEST);
        }

        boolean foundProjectName = projectRepository.hasProjectNameAlready(projectDTO.projectName());
        if (foundProjectName) {
            throw new ProjectException(ErrorMessages.PROJECT_INVALID, HttpStatus.BAD_REQUEST);
        }

        Project project = projectMapper.toProjectWithoutTasks(projectDTO);
        Workflow workflow = workflowService.getWorkflowById(GlobalEnum.DEFAULT_WORKFLOW.getId());
        if (workflow == null) {
            throw new WorkflowException(ErrorMessages.WORKFLOW_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        project.setWorkflow(workflow);
        projectRepository.save(project);

        return projectMapper.toDTOWithoutTasks(project);
    }

    private Map<StateDTO, List<StateDTO>> getWorkflowStateTransitionMap(long workflowId) {
        List<StateTransition>  stateTransitionList = stateTransitionService.getStatesTransitionsByWorkflowId(workflowId);
        List<StateTransitionDTO> stateTransitionDTOList = StateTransitionMapper.INSTANCE.toDTOWithList(stateTransitionList);
        return stateTransitionDTOList.stream()
                .collect(Collectors.groupingBy(StateTransitionDTO::fromStateDTO, Collectors.mapping(StateTransitionDTO::toStateDTO, Collectors.toList())));
    }
}
