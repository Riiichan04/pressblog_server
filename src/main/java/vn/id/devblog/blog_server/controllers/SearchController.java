package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.id.devblog.blog_server.dto.response.post.PublicPostResponse;
import vn.id.devblog.blog_server.services.SearchService;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/keyword")
    public ResponseEntity<Page<PublicPostResponse>> searchPosts(
            @RequestParam String q, //query
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty());
        }

        Page<PublicPostResponse> result = searchService.searchPosts(q.trim(), page, size);
        return ResponseEntity.ok(result);
    }
}
