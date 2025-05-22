package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;
import java.util.List;

@Builder
public record TaskDTO(
        long id,
        String taskName,
        String state,
        List<CommentDTO> commentDTOS,
        TaskMetadataDTO taskMetadataDTO
) {}
