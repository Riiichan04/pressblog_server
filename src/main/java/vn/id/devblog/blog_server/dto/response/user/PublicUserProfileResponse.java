package vn.id.devblog.blog_server.dto.response.user;

import java.time.LocalDateTime;

public record PublicUserProfileResponse(
        String username,
        String displayName,
        String description,
        String avatar,
        LocalDateTime joinedAt
) {}
