package vn.id.devblog.blog_server.dto.response.user;

public record AuthorStatsDto(
        String username,
        String avatar,
        long postCount,
        long viewCount
) {}