package dev.workforge.app.WorkForge.DTO;

import dev.workforge.app.WorkForge.Model.*;
import jakarta.persistence.*;

import java.util.Set;

public class TaskDTO implements DTO{

    private long id;

    private String taskName;

    private StateDTO stateDTO;

    private Set<AttachmentDTO> attachmentDTOS;

    private Set<CommentDTO> commentDTOS;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Embedded
    private TaskMetadata taskMetadata;
}
