package vn.id.devblog.blog_server.dto.request.user;

public record UpdateProfileRequest(
        String displayName,
        String bio,
        String avatar
) {}