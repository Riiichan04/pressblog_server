package vn.id.devblog.blog_server.dto.response.auth;

public record AuthResponse(
        boolean result,
        String message,
        AuthDto authDto
) { }
