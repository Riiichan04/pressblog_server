package vn.id.devblog.blog_server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.dto.request.post.CommentRequest;
import vn.id.devblog.blog_server.dto.request.post.PostRequest;
import vn.id.devblog.blog_server.dto.response.post.CommentResponse;
import vn.id.devblog.blog_server.dto.response.post.GetPostResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.models.Comment;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.models.Tag;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.CommentRepository;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

import java.util.stream.Collectors;

@Service
public class CommentService {
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse insertComment(CommentRequest commentRequest) {
        User owner = userRepository.findById(commentRequest.authorId()).orElse(null);
        Post post = postRepository.findById(commentRequest.postId()).orElse(null);
        if (owner == null || post == null) {
            return new PostResponse(false, "Invalid Request");
        }
        Comment comment = new Comment();
        comment.setAuthor(owner);
        comment.setPost(post);
        comment.setContent(commentRequest.content());
        if (commentRequest.commentId() != null) {
            comment.setParent(commentRepository.findById(commentRequest.commentId()).orElse(null));
        }
        else comment.setParent(null);
        commentRepository.save(comment);
        return new PostResponse(true, "Upload comment successfully");
    }

    @Transactional
    public PostResponse updateComment(Long id, CommentRequest commentRequest) {
        User owner = userRepository.findById(commentRequest.authorId()).orElse(null);
        Post post = postRepository.findById(commentRequest.postId()).orElse(null);
        Comment targetComment = commentRepository.findById(commentRequest.commentId()).orElse(null);
        if (owner == null || post == null || targetComment == null) {
            return new PostResponse(false, "Invalid Request");
        }
        targetComment.setContent(commentRequest.content());
        commentRepository.save(targetComment);
        return new PostResponse(true, "Update comment successfully");
    }

    @Transactional
    public PostResponse deleteComment(Long id) {
        Comment targetComment = commentRepository.findById(id).orElse(null);
        if (targetComment == null) {
            return new PostResponse(false, "Invalid Request");
        }
        targetComment.setDeleted(true);
        commentRepository.save(targetComment);
        return new PostResponse(true, "Deleted comment successfully");
    }

    public Page<CommentResponse> getCommentByPostId(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> listComment = commentRepository.findByPostId(postId, pageable);
        return this.mapToResponse(listComment);
    }

    public Page<CommentResponse> getReplyComment(Long commentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> listComment = commentRepository.findByParentId(commentId, pageable);
        return this.mapToResponse(listComment);
    }

    private Page<CommentResponse> mapToResponse(Page<Comment> comments) {
        return comments.map(comment ->
                new CommentResponse(
                        comment.getId(),
                        comment.getPost().getId(),
                        comment.getAuthor().getId(),
                        comment.getContent(),
                        comment.getUpvote(),
                        comment.getDownvote(),
                        comment.getParent().getId()
                )
        );
    }
}
