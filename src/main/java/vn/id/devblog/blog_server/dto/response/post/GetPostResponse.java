package vn.id.devblog.blog_server.dto.response.post;

import vn.id.devblog.blog_server.common.enums.PostStatus;

import java.util.Set;

public record GetPostResponse(
        Long id,
        String name,
        String slug,
        String content,
        String thumbnail,
        Long authorId,
        String categoryName,
        Set<String> tagNames,
        PostStatus status,
        int viewCount
) {
}
