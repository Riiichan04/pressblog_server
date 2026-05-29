package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.devblog.blog_server.dto.request.user.UpdatePasswordRequest;
import vn.id.devblog.blog_server.dto.response.auth.AuthResponse;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.services.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    UserService userService;

    @PutMapping("/password")
    public ResponseEntity<AuthResponse> updatePassword(
            @RequestBody UpdatePasswordRequest body,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(userService.updatePassword(currentUser.getId(), body.oldPassword(), body.newPassword()));
    }
}
