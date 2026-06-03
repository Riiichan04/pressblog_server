package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.dto.response.post.AdminPostResponse;
import vn.id.devblog.blog_server.dto.response.post.GetPostResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.services.AdminPostService;
import vn.id.devblog.blog_server.services.PostService;

@RestController
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {
    private final PostService postService;
    private final AdminPostService adminPostService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('UPDATE_ANY_POST', 'DELETE_ANY_POST', 'APPROVE_POST')")
    public ResponseEntity<Page<AdminPostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminPostService.getAllPosts(page, size));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FORCE_DELETE_ANY_POST')")
    public ResponseEntity<String> forceDeletePost(@PathVariable Long id) {
        boolean result = adminPostService.forceDeletePost(id);
        if (result) {
            return ResponseEntity.ok("Remove blog success");
        }
        return ResponseEntity.badRequest().body("Blog not found");
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('RESTORE_ANY_POST')")
    public ResponseEntity<String> restorePost(@PathVariable Long id) {
        boolean result = adminPostService.restorePost(id);
        if (result) {
            return ResponseEntity.ok("Restore blog success");
        }
        return ResponseEntity.badRequest().body("Blog not found");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('UPDATE_ANY_POST', 'APPROVE_POST')")
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

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public ResponseEntity<Page<AdminPostResponse>> getPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminPostService.getPostsByStatus(PostStatus.PENDING, page, size));
    }

    @GetMapping("/{slug}")
    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public ResponseEntity<GetPostResponse> getAdminPostBySlug(@PathVariable String slug) {
        GetPostResponse response = postService.getPostBySlug(slug, PostStatus.PENDING);
        return ResponseEntity.ok(response);
    }
}