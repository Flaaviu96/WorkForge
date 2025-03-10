package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

import java.util.Set;

@Builder
public record TaskDTO(
        long id,
        String taskName,
        StateDTO stateDTO,
        Set<AttachmentDTO> attachmentDTOS,
        Set<CommentDTO> commentDTOS,
        ProjectDTO projectDTO,
        TaskMetadataDTO taskMetadataDTO
) {}
