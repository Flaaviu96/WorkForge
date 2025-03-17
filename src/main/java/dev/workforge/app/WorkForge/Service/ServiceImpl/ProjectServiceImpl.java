package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Exceptions.ProjectNotFoundException;
import dev.workforge.app.WorkForge.Mapper.ProjectMapper;
import dev.workforge.app.WorkForge.Model.Project;
import dev.workforge.app.WorkForge.Repository.ProjectRepository;
import dev.workforge.app.WorkForge.Service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<TaskDTO> getTasksWithCommentsByProjectId(long projectId) {
        Project project = projectRepository.findTasksWithCommentsByProjectId(projectId)
                .orElseThrow(ProjectNotFoundException::new);
        return ProjectMapper.INSTANCE.toDTOWithTasks(project).taskDTO();
    }

    @Override
    public List<Project> getProjectsByProjectIds(List<Long> projectIds) {
        return List.of();
    }

//    public List<ProjectDTO> getProjectsWithoutTasks(Page page) {
//
//    }
}
