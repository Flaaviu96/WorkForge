package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.CreateProjectDTO;
import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.Model.Project;
import dev.workforge.app.WorkForge.Projections.ProjectProjection;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.util.*;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface ProjectMapper {

    ProjectDTO toDTOWithTasks(ProjectProjection projectProjection);

    Project createProject(CreateProjectDTO createProjectDTO);

    @Named("toDTOWithoutTasks")
    @Mapping(target = "tasks", ignore = true)
    ProjectDTO toDTOWithoutTasks(Project project);

    @IterableMapping(qualifiedByName = "toDTOWithoutTasks")
    List<ProjectDTO> toProjectsDTOWithoutTasks(List<Project> projects);
}
