package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.*;
import dev.workforge.app.WorkForge.Exceptions.ProjectException;
import dev.workforge.app.WorkForge.Mapper.ProjectMapper;
import dev.workforge.app.WorkForge.Mapper.TaskMapper;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Model.Project;
import dev.workforge.app.WorkForge.Projections.TaskProjection;
import dev.workforge.app.WorkForge.Repository.ProjectRepository;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.ProjectReadService;
import dev.workforge.app.WorkForge.Service.TaskService;
import dev.workforge.app.WorkForge.Util.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectReadServiceImpl implements ProjectReadService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    public ProjectReadServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, TaskMapper taskMapper, TaskService taskService, TaskService taskService1) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.taskMapper = taskMapper;
        this.taskService = taskService1;
    }

    @Override
    public List<TaskDTO> getTasksWithoutCommentsByProjectId(long projectId) {
        List<TaskProjection> taskProjections = projectRepository.findTaskSummariesByProjectId(projectId)
                .orElseThrow(() -> new ProjectException(ErrorMessages.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND));
        return taskMapper.taskProjectionToDTO(taskProjections);
    }

    @Override
    public List<TaskDTO> getTasksWithSummaries(long projectId) {
        List<TaskProjection> taskProjections = projectRepository.findTaskSummariesByProjectId(projectId)
                .orElseThrow(() -> new ProjectException(ErrorMessages.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (taskProjections.isEmpty()) {
            throw new ProjectException("No tasks found for project", HttpStatus.NOT_FOUND);
        }

        return taskMapper.taskProjectionToDTO(taskProjections);
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

    @Override
    public Long getProjectIdBasedOnProjectKey(String projectKey) {
        if (projectKey != null && !projectKey.isBlank() && !projectKey.isEmpty()) {
            Project project = projectRepository.findProjectByProjectKey(projectKey)
                    .orElseThrow(()-> new ProjectException(ErrorMessages.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND));
            return project.getId();
        }
        return 0L;
    }

    @Override
    public PageResultDTO<TaskSummaryDTO> getTasksByFilter(TaskFilter taskFilter, long projectId) {
        return taskService.getPaginatedTaskSummaries(taskFilter, projectId);
    }
}
