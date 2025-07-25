package dev.workforge.app.WorkForge.DTO;

import dev.workforge.app.WorkForge.Model.PermissionType;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record TaskDTO(
        long id,
        List<PermissionType> permissionTypes,
        String taskName,
        String state,
        List<CommentDTO> commentDTOS,
        List<AttachmentDTO> attachmentDTOS,
        TaskMetadataDTO taskMetadataDTO,
        TaskTimeTrackingDTO taskTimeTrackingDTO,
        Date createdDate,
        Date modifiedDate
) {}
