package vn.id.devblog.blog_server.dto.request.post;

import vn.id.devblog.blog_server.common.enums.PostLanguage;

import java.util.Set;

public record PostRequest(
        String name,
        String content,
        String thumbnail,
        String email,
        String excerpt,
        String categoryName,
        Set<String> listTag,
        PostLanguage language
) {
}