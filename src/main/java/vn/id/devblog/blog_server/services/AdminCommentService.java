package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.common.enums.CommentStatus;
import vn.id.devblog.blog_server.dto.response.post.AdminCommentResponse;
import vn.id.devblog.blog_server.models.Comment;
import vn.id.devblog.blog_server.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCommentService {

    private final CommentRepository commentRepository;

    public Page<AdminCommentResponse> getAllComments(int page, int size, String postSlug, CommentStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments;

        // Rẽ nhánh logic kết hợp Lọc theo Bài viết & Lọc theo Trạng thái
        if (postSlug != null && !postSlug.isEmpty()) {
            if (status != null) {
                comments = commentRepository.findByPost_SlugAndStatus(postSlug, status, pageable);
            } else {
                comments = commentRepository.findByPost_Slug(postSlug, pageable);
            }
        } else {
            if (status != null) {
                comments = commentRepository.findByStatus(status, pageable);
            } else {
                comments = commentRepository.findAll(pageable);
            }
        }

        return comments.map(comment -> new AdminCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor() != null ? comment.getAuthor().getDisplayName() : "Anonymous",
                comment.getPost() != null ? comment.getPost().getId() : -1,
                comment.getPost() != null ? comment.getPost().getName() : "Blog deleted",
                comment.isDeleted(),
                comment.getCreatedAt(),
                comment.getStatus()
        ));
    }

    @Transactional
    public boolean approveComment(Long id, CommentStatus status) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            log.warn("Comment id {} not found for approval", id);
            return false;
        }

        comment.setStatus(status);

        commentRepository.save(comment);
        log.info("Comment id {} has been {}", id, status);
        return true;
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