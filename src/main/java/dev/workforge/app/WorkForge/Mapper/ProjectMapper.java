package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.ProjectDTO;
import dev.workforge.app.WorkForge.Model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "tasks", source = "tasks")
    ProjectDTO toDTOWithTasks(Project project);

    @Mapping(target = "tasks", ignore = true)
    ProjectDTO toDTOWithoutTasks(Project project);
}
