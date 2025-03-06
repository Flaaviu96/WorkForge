package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.Model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "taskDTOS", source = "tasks")
    ProjectDTO toDTOWithTasks(Project project);

    @Mapping(target = "taskDTOS", ignore = true)
    ProjectDTO toDTOWithoutTasks(Project project);

    @Mapping(target = "tasks", source = "taskDTOS")
    Project toProjectWithTasks(ProjectDTO projectDTO);

    @Mapping(target = "tasks", ignore = true)
    Project toProjectWithoutTasks(ProjectDTO projectDTO);
}
