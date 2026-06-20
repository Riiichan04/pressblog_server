package vn.id.devblog.blog_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.common.constants.RoleConstants;
import vn.id.devblog.blog_server.dto.request.page.StaticPageRequest;
import vn.id.devblog.blog_server.dto.response.page.GetStaticPageResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.services.StaticPageService;

@RestController
@RequestMapping("/pages")
public class StaticPageController {

    private final StaticPageService staticPageService;

    @Autowired
    public StaticPageController(StaticPageService staticPageService) {
        this.staticPageService = staticPageService;
    }

    /**
     * PUBLIC API
     * Endpoint: GET /pages/{slug}
     */
    @GetMapping("/{slug}")
    public ResponseEntity<GetStaticPageResponse> getPublicPage(@PathVariable String slug) {
        GetStaticPageResponse response = staticPageService.getPublicPageBySlug(slug);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // ADMIN APIs
    /**
     * Endpoint: GET /pages/admin/all?page=0&size=10
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<Page<GetStaticPageResponse>> getAllPagesForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GetStaticPageResponse> pages = staticPageService.getAllPagesForAdmin(page, size);
        return ResponseEntity.ok(pages);
    }

    /**
     * Endpoint: GET /pages/admin/{id}
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<GetStaticPageResponse> getPageByIdForAdmin(@PathVariable Long id) {
        GetStaticPageResponse response = staticPageService.getPageByIdForAdmin(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: POST /pages
     */
    @PostMapping
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<PostResponse> createPage(@RequestBody StaticPageRequest request) {
        PostResponse response = staticPageService.createPage(request);
        if (!response.result()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint: PUT /pages/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<PostResponse> updatePage(
            @PathVariable Long id,
            @RequestBody StaticPageRequest request) {
        PostResponse response = staticPageService.updatePage(id, request);
        if (!response.result()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: DELETE /pages/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<PostResponse> deletePage(@PathVariable Long id) {
        PostResponse response = staticPageService.deletePage(id);
        if (!response.result()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }
}