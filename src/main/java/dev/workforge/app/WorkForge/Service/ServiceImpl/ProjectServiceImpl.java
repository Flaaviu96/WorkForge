package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Exceptions.ProjectNotFoundException;
import dev.workforge.app.WorkForge.Mapper.ProjectMapper;
import dev.workforge.app.WorkForge.Model.Project;
import dev.workforge.app.WorkForge.Repository.ProjectRepository;
import dev.workforge.app.WorkForge.Service.ProjectService;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import org.hibernate.query.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserPermissionService userPermissionService;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserPermissionService userPermissionService) {
        this.projectRepository = projectRepository;
        this.userPermissionService = userPermissionService;
    }

    @Override
    public List<TaskDTO> getTasksWithCommentsByProjectId(long projectId) {
        Project project = projectRepository.findTasksWithCommentsByProjectId(projectId)
                .orElseThrow(ProjectNotFoundException::new);
        return ProjectMapper.INSTANCE.toDTOWithTasks(project).taskDTO();
    }

    public List<ProjectDTO> getProjectsWithoutTasks(Page page) {

    }
}
