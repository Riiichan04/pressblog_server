package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.response.post.PublicPostResponse;
import vn.id.devblog.blog_server.dto.response.user.PublicUserProfileResponse;
import vn.id.devblog.blog_server.services.UserPublicService;
@RestController
@RequestMapping("/user/author")
@RequiredArgsConstructor
public class UserPublicController {
    private final UserPublicService publicUserService;

    @GetMapping("/{username}")
    public ResponseEntity<PublicUserProfileResponse> getAuthorProfile(@PathVariable String username) {
        PublicUserProfileResponse response = publicUserService.getPublicProfile(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<Page<PublicPostResponse>> getAuthorPosts(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PublicPostResponse> posts = publicUserService.getPublicPostsByAuthor(username, page, size);
        return ResponseEntity.ok(posts);
    }
}
