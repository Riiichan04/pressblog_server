package vn.id.devblog.blog_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.request.post.CommentRequest;
import vn.id.devblog.blog_server.dto.response.post.CommentResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.services.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public ResponseEntity<PostResponse> addComment(@RequestBody CommentRequest commentRequest) {
        PostResponse response = commentService.insertComment(commentRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<PostResponse> editComment(@RequestParam Long id, @RequestBody CommentRequest commentRequest) {
        PostResponse response = commentService.updateComment(id, commentRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<PostResponse> deleteComment(@RequestParam Long id) {
        PostResponse response = commentService.deleteComment(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{postId}")
    public ResponseEntity<Page<CommentResponse>> getComment(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam(defaultValue = "10") int page
    ) {
        Page<CommentResponse> responses = commentService.getCommentByPostId(postId, size, page);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reply/get/{commentId}")
    public ResponseEntity<Page<CommentResponse>> getCommentReply(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam(defaultValue = "10") int page
    ) {
        Page<CommentResponse> responses = commentService.getReplyComment(commentId, size, page);
        return ResponseEntity.ok(responses);
    }
}
