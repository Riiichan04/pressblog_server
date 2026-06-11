package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.devblog.blog_server.dto.response.post.GetPostResponse;
import vn.id.devblog.blog_server.dto.response.user.AuthorStatsDto;
import vn.id.devblog.blog_server.services.PostMetadataService;
import vn.id.devblog.blog_server.services.PostService;
import vn.id.devblog.blog_server.services.UserPublicService;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;
    private final UserPublicService userPublicService;
    private final PostMetadataService postMetadataService;

    @GetMapping("/featured")
    public ResponseEntity<GetPostResponse> getFeaturedPost() {
        return ResponseEntity.ok(postService.getFeaturedPost());
    }

    @GetMapping("/newest")
    public ResponseEntity<List<GetPostResponse>> getNewestPost() {
        return ResponseEntity.ok(postService.getNewestPost());
    }

    @GetMapping("/trending-tags")
    public ResponseEntity<List<String>> getTrendingTags() {
        return ResponseEntity.ok(postMetadataService.getTrendingTags());
    }

    @GetMapping("/featured-authors")
    public ResponseEntity<List<AuthorStatsDto>> getFeaturedAuthors() {
        return ResponseEntity.ok(userPublicService.getFeaturedAuthors());
    }
}
