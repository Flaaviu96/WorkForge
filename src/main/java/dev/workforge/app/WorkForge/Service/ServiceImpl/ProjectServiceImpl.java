package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Exceptions.ProjectNotFoundException;
import dev.workforge.app.WorkForge.Mapper.ProjectMapper;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Model.Project;
import dev.workforge.app.WorkForge.Repository.ProjectRepository;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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

    @PermissionCheck(permissionType = {PermissionType.READ})
    @Override
    public List<ProjectDTO> getProjectsWithoutTasks(List<Long> projectsIds, Pageable page) {
        Page<Project> projectsPage = projectRepository.findProjectsByPage(page);
        if (!projectsPage.isEmpty()) {
            return ProjectMapper.INSTANCE.toProjectsDTOWithoutTasks(projectsPage.stream().toList());
        }

        throw new ProjectNotFoundException("No projects found");
    }

    @Override
    public void saveProject(ProjectDTO projectDTO) {
        boolean foundProjectName = projectRepository.hasProjectNameAlready(projectDTO.projectName());
        if (foundProjectName) {
            return;
        }

    }
}
