package vn.id.devblog.blog_server.dto.response.user;

import java.time.LocalDateTime;

public record AdminUserResponse(
        Long id,
        String username,
        String email,
        String displayName,
        String roleName,
        boolean active,
        boolean verified,
        LocalDateTime createdAt,
        String avatarUrl
) {
}