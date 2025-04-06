package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Enum.GlobalEnum;
import dev.workforge.app.WorkForge.Exceptions.ProjectNotFoundException;
import dev.workforge.app.WorkForge.Exceptions.ProjectUpdateFailedException;
import dev.workforge.app.WorkForge.Mapper.ProjectMapper;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.*;
import dev.workforge.app.WorkForge.Repository.ProjectRepository;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.ProjectService;
import dev.workforge.app.WorkForge.Service.StateService;
import dev.workforge.app.WorkForge.Service.WorkflowService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final StateService service;
    private final WorkflowService workflowService;

    public ProjectServiceImpl(ProjectRepository projectRepository, StateService service, WorkflowService workflowService) {
        this.projectRepository = projectRepository;
        this.service = service;
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
    public void saveNewTaskIntoProject(long projectId, TaskDTO taskDTO) {
        for (int i = 0; i < 3; i++) {
            try {
                Optional<Project> optionalProject = projectRepository.findProjectIdWithTasks(projectId);
                if (optionalProject.isEmpty()) {
                    throw new ProjectNotFoundException("Project not found for the given ID.");
                }
                optionalProject.ifPresent(project -> createTaskAndAssignIt(project, taskDTO));
            } catch (OptimisticLockException e) {
                throw new ProjectUpdateFailedException("Project update failed after multiple attempts.");
            }
        }
    }

    private void createTaskAndAssignIt(Project project, TaskDTO taskDTO) {
        State newState = service.loadStateByStateType(StateType.INITIAL);
        Task task = TaskMapper.INSTANCE.toTask(taskDTO);
        task.setProject(project);
        task.setId(project.getId());
        task.setState(newState);
        project.getTasks().add(task);
    }


    @Override
    public void saveNewProject(ProjectDTO projectDTO) {
        boolean foundProjectName = projectRepository.hasProjectNameAlready(projectDTO.projectName());
        if (foundProjectName) {
            return;
        }

        Project project = ProjectMapper.INSTANCE.toProjectWithoutTasks(projectDTO);
        Workflow workflow = workflowService.getWorkflowById(GlobalEnum.DEFAULT_WORKFLOW.getId());

        if (workflow != null) {
            project.setWorkflow(workflow);
            projectRepository.save(project);
        }
    }
}
