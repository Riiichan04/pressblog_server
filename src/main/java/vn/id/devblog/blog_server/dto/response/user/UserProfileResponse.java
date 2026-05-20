package vn.id.devblog.blog_server.dto.response.user;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String displayName,
        String bio,
        String avatar,
        LocalDateTime createdAt
) {}
