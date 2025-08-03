package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.Model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
