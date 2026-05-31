package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.common.constants.PermissionConstants;
import vn.id.devblog.blog_server.common.constants.RoleConstants;
import vn.id.devblog.blog_server.dto.request.post.CommentRequest;
import vn.id.devblog.blog_server.dto.response.post.CommentResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.models.Comment;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.CommentRepository;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostResponse insertComment(CommentRequest commentRequest, User author) {
        Post post = postRepository.findById(commentRequest.postId()).orElse(null);
        if (post == null) {
            return new PostResponse(false, "Invalid Request");
        }
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setContent(commentRequest.content());
        if (commentRequest.commentId() != null) {
            comment.setParent(commentRepository.findById(commentRequest.commentId()).orElse(null));
        } else comment.setParent(null);
        commentRepository.save(comment);
        return new PostResponse(true, "Upload comment successfully");
    }

    @Transactional
    public PostResponse updateComment(Long commentId, CommentRequest commentRequest, User user) {
        Post post = postRepository.findById(commentRequest.postId()).orElse(null);
        Comment targetComment = commentRepository.findById(commentId).orElse(null);
        if (user == null || post == null || targetComment == null) {
            return new PostResponse(false, "Invalid Request");
        }
        if (!targetComment.getAuthor().getId().equals(user.getId())) {
            return new PostResponse(false, "You don't have permission to edit this comment");
        }
        targetComment.setContent(commentRequest.content());
        commentRepository.save(targetComment);
        return new PostResponse(true, "Update comment successfully");
    }

    @Transactional
    public PostResponse deleteComment(Long id, User user) {
        Comment targetComment = commentRepository.findById(id).orElse(null);
        if (targetComment == null) {
            return new PostResponse(false, "Invalid Request");
        }
        boolean hasAdminRight = user.getAuthorities().stream()
                .anyMatch(auth ->
                        Objects.equals(auth.getAuthority(), PermissionConstants.DELETE_ANY_COMMENT) ||
                                Objects.equals(auth.getAuthority(), RoleConstants.ROLE_ADMIN)
                );

        boolean isOwnerComment = targetComment.getAuthor().getId().equals(user.getId());

        if (!isOwnerComment && !hasAdminRight) {
            return new PostResponse(false, "You don't have permission to delete this comment");
        }

        targetComment.setDeleted(true);
        commentRepository.save(targetComment);
        return new PostResponse(true, "Deleted comment successfully");
    }

    public Page<CommentResponse> getCommentByPostId(Long postId, int page, int size) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || post.isDeleted() || !post.getStatus().name().equals("APPROVED")) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> listComment = commentRepository.findByPostIdAndParentIdIsNullAndIsDeletedFalseOrderByCreatedAtDesc(postId, pageable);
        return this.mapToResponse(listComment);
    }
    public Page<CommentResponse> getReplyComment(Long commentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> listComment = commentRepository.findByParentId(commentId, pageable);
        return this.mapToResponse(listComment);
    }

    private Page<CommentResponse> mapToResponse(Page<Comment> comments) {
        return comments.map(comment -> {
            Long parentId = comment.getParent() != null ? comment.getParent().getId() : null;
            String authorDisplayName = comment.getAuthor().getDisplayName() != null ? comment.getAuthor().getDisplayName() : comment.getAuthor().getUsername();
            int replyCount = commentRepository.countByParentId(comment.getId());

            return new CommentResponse(
                    comment.getId(),
                    comment.getPost().getId(),
                    comment.getAuthor().getAvatar(),
                    authorDisplayName,
                    comment.getContent(),
                    comment.getUpvote(),
                    comment.getDownvote(),
                    parentId,
                    replyCount,
                    comment.getCreatedAt()
            );
        });
    }
}
