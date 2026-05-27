package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.request.post.CommentRequest;
import vn.id.devblog.blog_server.dto.response.post.CommentResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.services.CommentService;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<PostResponse> addComment(@RequestBody CommentRequest commentRequest) {
        PostResponse response = commentService.insertComment(commentRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> editComment(
            @PathVariable Long id,
            @RequestBody CommentRequest commentRequest
    ) {
        PostResponse response = commentService.updateComment(id, commentRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostResponse> deleteComment(@PathVariable Long id) {
        PostResponse response = commentService.deleteComment(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Page<CommentResponse>> getComment(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentResponse> responses = commentService.getCommentByPostId(postId, page, size);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<CommentResponse>> getCommentReply(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentResponse> responses = commentService.getReplyComment(commentId, page, size);
        return ResponseEntity.ok(responses);
    }
}