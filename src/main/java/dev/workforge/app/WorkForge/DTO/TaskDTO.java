package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;
import java.util.List;

@Builder
public record TaskDTO(
        long id,
        String taskName,
        StateDTO stateDTO,
        List<AttachmentDTO> attachmentDTOS,
        List<CommentDTO> commentDTOS,
        ProjectDTO projectDTO,
        TaskMetadataDTO taskMetadataDTO
) {}
