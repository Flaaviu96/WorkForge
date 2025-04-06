package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDTO toDTO(Task task);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Task toTask(TaskDTO taskDTO);

    @Mapping(target = "commentDTO", source = "comments")
    List<TaskDTO> toDTO (Set<Task> tasks);
}