package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.common.enums.CommentStatus;
import vn.id.devblog.blog_server.dto.response.post.AdminCommentResponse;
import vn.id.devblog.blog_server.services.AdminCommentService;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping
    public ResponseEntity<Page<AdminCommentResponse>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String postSlug,
            @RequestParam(required = false) CommentStatus status
    ) {
        return ResponseEntity.ok(adminCommentService.getAllComments(page, size, postSlug, status));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('APPROVE_COMMENT')")
    public ResponseEntity<String> approveComment(
            @PathVariable Long id,
            @RequestBody CommentStatus commentStatus
    ) {
        boolean result = adminCommentService.approveComment(id, commentStatus);
        if (result) {
            return ResponseEntity.ok("Comment approved successfully");
        }
        return ResponseEntity.badRequest().body("Comment not found or already approved");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_ANY_COMMENT')")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        boolean result = adminCommentService.deleteComment(id);
        if (result) {
            return ResponseEntity.ok("Comment removed");
        }
        return ResponseEntity.badRequest().body("Comment not found or already deleted");
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('RESTORE_ANY_COMMENT')")
    public ResponseEntity<String> restoreComment(@PathVariable Long id) {
        boolean result = adminCommentService.restoreComment(id);
        if (result) {
            return ResponseEntity.ok("Restored comment successfully");
        }
        return ResponseEntity.badRequest().body("Comment not found");
    }
}