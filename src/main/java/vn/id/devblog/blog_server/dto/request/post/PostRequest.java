package vn.id.devblog.blog_server.dto.request.post;

import java.util.List;
import java.util.Set;

public record PostRequest(
        String name,
        String content,
        String thumbnail,
        String email,
        String categoryName,
        Set<String> listTag
) {
}