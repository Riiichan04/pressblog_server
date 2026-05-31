package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.response.user.AdminUserResponse;
import vn.id.devblog.blog_server.services.AdminUserService;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminUserService.getAllUsers(page, size));
    }

    //Update user status by admin.
    //Admin can ban or unban account
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        boolean result = adminUserService.toggleUserStatus(id);
        if (result) {
            return ResponseEntity.ok("Update user status success");
        }
        return ResponseEntity.badRequest().body("Update user status failed or user not found");
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<String> changeUserRole(
            @PathVariable Long id,
            @RequestParam String roleName
    ) {
        boolean result = adminUserService.changeUserRole(id, roleName);
        if (result) {
            return ResponseEntity.ok("Update user role success");
        }
        return ResponseEntity.badRequest().body("Update user role failed or user not found");
    }
}