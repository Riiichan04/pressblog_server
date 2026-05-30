package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.devblog.blog_server.dto.response.post.GetPostResponse;
import vn.id.devblog.blog_server.services.PostService;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;

    @GetMapping("/featured")
    public ResponseEntity<GetPostResponse> getFeaturedPost() {
        return ResponseEntity.ok(postService.getFeaturedPost());
    }

    @GetMapping("/newest")
    public ResponseEntity<List<GetPostResponse>> getNewestPost() {
        return ResponseEntity.ok(postService.getNewestPost());
    }
}
