package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.models.Tag;
import vn.id.devblog.blog_server.services.AdminTagService;

import java.util.Map;

@RestController
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final AdminTagService adminTagService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CONTENT_MOD')")
    public ResponseEntity<Page<Tag>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Tag> res = adminTagService.getAllTagsForAdmin(page, size);
        return ResponseEntity.ok(adminTagService.getAllTagsForAdmin(page, size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_TAG')")
    public ResponseEntity<Tag> updateTag(@PathVariable int id, @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(adminTagService.updateTag(id, payload.get("name")));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('APPROVE_TAG')")
    public ResponseEntity<Tag> toggleApproval(@PathVariable int id) {
        return ResponseEntity.ok(adminTagService.toggleApproval(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_TAG')")
    public ResponseEntity<Void> forceDeleteTag(@PathVariable int id) {
        adminTagService.forceDeleteTag(id);
        return ResponseEntity.noContent().build();
    }
}