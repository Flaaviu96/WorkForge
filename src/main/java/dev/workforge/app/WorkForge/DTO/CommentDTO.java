package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

import java.util.Date;

@Builder
public record CommentDTO(
        long id,
        String author,
        String content,
        Date createdDate,
        Date modifiedDate
) {}