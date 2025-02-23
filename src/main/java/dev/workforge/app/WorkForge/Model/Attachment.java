package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_seq")
    @SequenceGenerator(name = "attachment_seq", sequenceName = "attachment_id_seq", allocationSize = 50)
    private long id;

    private String fileName;

    private String fileType;

    private String path;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
