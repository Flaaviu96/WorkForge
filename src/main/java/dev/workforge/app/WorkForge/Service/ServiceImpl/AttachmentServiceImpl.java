package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.AttachmentDTO;
import dev.workforge.app.WorkForge.Exceptions.AttachmentException;
import dev.workforge.app.WorkForge.Mapper.AttachmentMapper;
import dev.workforge.app.WorkForge.Model.Attachment;
import dev.workforge.app.WorkForge.Model.Task;
import dev.workforge.app.WorkForge.Repository.AttachmentRepository;
import dev.workforge.app.WorkForge.Service.AttachmentService;
import dev.workforge.app.WorkForge.Util.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final FileServiceImpl fileService;
    private final AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(FileServiceImpl fileService, AttachmentRepository attachmentRepository) {
        this.fileService = fileService;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public AttachmentDTO saveNewAttachment(Task task, MultipartFile file, long projectId) throws IOException {
        boolean duplicate = task.getAttachments().stream()
                .anyMatch(attachment -> attachment.getFileName().equals(file.getOriginalFilename()));
        if (duplicate) {
            throw new AttachmentException(ErrorMessages.ATTACHMENT_DUPLICATE, HttpStatus.BAD_REQUEST);
        }

        Path path = fileService.saveFile(file, task.getId(), task.getProject().getProjectName());
        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setPath(path.toString());
        attachment.setFileName(file.getOriginalFilename());
        attachment.setProjectId(task.getProject().getId());
        attachment.setSize(file.getSize());

        task.getAttachments().add(attachment);
        Attachment savedAttachment = attachmentRepository.saveAndFlush(attachment);
        return AttachmentMapper.INSTANCE.toDTO(savedAttachment);
    }

    @Override
    public Attachment downloadAttachment(Task task, long attachmentId) {
        Optional<Attachment> optionalAttachment = task.getAttachments().stream().filter(attachment -> attachment.getId() == attachmentId).findFirst();
        if (optionalAttachment.isPresent()) {
            return optionalAttachment.get();
        }
        throw new AttachmentException(ErrorMessages.ATTACHMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @Override
    public void deleteAttachment(Task task, long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new AttachmentException(ErrorMessages.ATTACHMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        fileService.deleteFile(attachment.getPath());
        attachment.setTask(null);

        attachmentRepository.delete(attachment);
    }
}
