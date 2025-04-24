package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.Comment;
import dev.workforge.app.WorkForge.Repository.CommentRepository;
import dev.workforge.app.WorkForge.Service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment saveNewComment(Comment comment) {
        if (hasRequiredFields(comment)) {
            return commentRepository.save(comment);
        }
        return null;
    }

    private boolean hasRequiredFields(Comment comment) {
        return comment.getAuthor() != null && !comment.getAuthor().isEmpty()
                && comment.getContent() != null && !comment.getContent().isEmpty();
    }
}
