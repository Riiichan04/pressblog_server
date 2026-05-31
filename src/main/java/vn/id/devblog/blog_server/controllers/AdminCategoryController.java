package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.request.post.CategoryRequest;
import vn.id.devblog.blog_server.models.Category;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.services.AdminCategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        try {
            Category savedCategory = adminCategoryService.createCategory(request);
            return ResponseEntity.ok(savedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody CategoryRequest request) {
        Category updatedCategory = adminCategoryService.updateCategory(id, request);
        if (updatedCategory != null) {
            return ResponseEntity.ok(updatedCategory);
        }
        return ResponseEntity.badRequest().body("Category not found");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> safeDeleteCategory(@PathVariable Integer id) {
        try {
            boolean result = adminCategoryService.deleteCategory(id);
            if (result) {
                return ResponseEntity.ok("Delete category success");
            }
            return ResponseEntity.badRequest().body("Category not found or detele failed");

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/force")
    public ResponseEntity<?> forceDeleteCategory(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        boolean result = adminCategoryService.forceDeleteCategory(id, user);
        if (result) {
            return ResponseEntity.ok("Force delete category success");
        }
        return ResponseEntity.badRequest().body("Category not found or failed to force delete this category");
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreCategory(@PathVariable Integer id) {
        boolean result = adminCategoryService.restoreCategory(id);
        if (result) {
            return ResponseEntity.ok("Restore category success");
        }
        return ResponseEntity.badRequest().body("Category not found for failed to restore this category");
    }
}