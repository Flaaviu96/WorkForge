package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.CommentDTO;
import dev.workforge.app.WorkForge.Model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDTO toCommentDTO(Comment comment);
}
