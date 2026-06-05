package vn.id.devblog.blog_server.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.request.auth.LoginRequest;
import vn.id.devblog.blog_server.dto.request.auth.RegisterRequest;
import vn.id.devblog.blog_server.dto.response.auth.AuthDto;
import vn.id.devblog.blog_server.dto.response.auth.AuthResponse;
import vn.id.devblog.blog_server.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = this.authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = this.authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDto> getCurrentUser(
            Authentication authentication
    ) {
        String username = authentication.getName();
        AuthDto authDto = authService.getAuthDtoByUsername(username);
        return ResponseEntity.ok(authDto);
    }
}
