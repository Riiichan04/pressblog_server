package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.request.post.CategoryRequest;
import vn.id.devblog.blog_server.models.Category;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.services.AdminCategoryService;
import vn.id.devblog.blog_server.services.PostMetadataService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;
    private final PostMetadataService postMetadataService;

    @GetMapping
    @PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
    public ResponseEntity<Page<Category>> getAllCategoriesByAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postMetadataService.getPaginationCategories(PageRequest.of(page, size)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CATEGORY')")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        try {
            Category savedCategory = adminCategoryService.createCategory(request);
            return ResponseEntity.ok(savedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody CategoryRequest request) {
        Category updatedCategory = adminCategoryService.updateCategory(id, request);
        if (updatedCategory != null) {
            return ResponseEntity.ok(updatedCategory);
        }
        return ResponseEntity.badRequest().body("Category not found");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_CATEGORY')")
    public ResponseEntity<?> safeDeleteCategory(@PathVariable Integer id) {
        try {
            boolean result = adminCategoryService.deleteCategory(id);
            if (result) {
                return ResponseEntity.ok("Delete category success");
            }
            return ResponseEntity.badRequest().body("Can't delete category or category not found");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/force")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> forceDeleteCategory(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        boolean result = adminCategoryService.forceDeleteCategory(id, user);
        if (result) {
            return ResponseEntity.ok("Force delete category success");
        }
        return ResponseEntity.badRequest().body("Category not found or failed to force delete this category");
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
    public ResponseEntity<?> restoreCategory(@PathVariable Integer id) {
        boolean result = adminCategoryService.restoreCategory(id);
        if (result) {
            return ResponseEntity.ok("Restore category success");
        }
        return ResponseEntity.badRequest().body("Category not found for failed to restore this category");
    }
}