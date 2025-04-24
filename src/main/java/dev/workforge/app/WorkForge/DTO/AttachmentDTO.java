package dev.workforge.app.WorkForge.DTO;

import lombok.Builder;

@Builder
public record AttachmentDTO(
        long id,
        String fileName,
        String fileType
) {}
