package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.TaskDTO;
import dev.workforge.app.WorkForge.Model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDTO toDTO(Task task);

    Task toTask(TaskDTO taskDTO);

    @Mapping(target = "commentDTOS", source = "comments")
    Set<TaskDTO> toDTO (Set<Task> tasks);
}