package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.dto.response.post.AdminCommentResponse;
import vn.id.devblog.blog_server.models.Comment;
import vn.id.devblog.blog_server.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCommentService {

    private final CommentRepository commentRepository;

    public Page<AdminCommentResponse> getAllComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findAll(pageable);

        return comments.map(comment -> new AdminCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getUsername(),
                comment.getPost() != null ? comment.getPost().getId() : null,
                comment.getPost() != null ? comment.getPost().getName() : "Blog deleted",
                comment.isDeleted(),
                comment.getCreatedAt()
        ));
    }

    @Transactional
    public boolean deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null || comment.isDeleted()) {
            return false;
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
        log.info("Admin/Mod deleted comment id {}", id);
        return true;
    }

    @Transactional
    public boolean restoreComment(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null || !comment.isDeleted()) {
            return false;
        }

        comment.setDeleted(false);
        commentRepository.save(comment);
        log.info("Admin/Mod restored comment id {}", id);
        return true;
    }
}