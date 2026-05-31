package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.dto.response.post.AdminPostResponse;
import vn.id.devblog.blog_server.services.AdminPostService;

@RestController
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    private final AdminPostService adminPostService;

    @GetMapping
    public ResponseEntity<Page<AdminPostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminPostService.getAllPosts(page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> forceDeletePost(@PathVariable Long id) {
        boolean result = adminPostService.forceDeletePost(id);
        if (result) {
            return ResponseEntity.ok("Remove blog success");
        }
        return ResponseEntity.badRequest().body("Blog not found");
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<String> restorePost(@PathVariable Long id) {
        boolean result = adminPostService.restorePost(id);
        if (result) {
            return ResponseEntity.ok("Restore blog success");
        }
        return ResponseEntity.badRequest().body("Blog not found");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updatePostStatus(
            @PathVariable Long id,
            @RequestParam PostStatus status
    ) {
        boolean result = adminPostService.updatePostStatus(id, status);
        if (result) {
            return ResponseEntity.ok("Update blog status to: " + status);
        }
        return ResponseEntity.badRequest().body("Update status failed or blog not found");
    }
}