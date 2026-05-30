package vn.id.devblog.blog_server.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.request.post.PostRequest;
import vn.id.devblog.blog_server.dto.response.post.GetPostResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.services.PostService;
import vn.id.devblog.blog_server.services.PostViewService;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostViewService postViewService;

    //Add new blog
    @PostMapping
    public ResponseEntity<PostResponse> uploadNewPost(
            @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        PostResponse response = postService.insertNewPost(postRequest, currentUser);
        return ResponseEntity.ok(response);
    }

    //Edit blog
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> editPost(
            @PathVariable Long id,
            @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        PostResponse response = postService.updatePost(id, postRequest, currentUser);
        return ResponseEntity.ok(response);
    }

    //Delete blog
    @DeleteMapping("/{id}")
    public ResponseEntity<PostResponse> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        PostResponse response = postService.deletePost(id, currentUser);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/author/{id}")
    public ResponseEntity<Page<GetPostResponse>> getPostByAuthor(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetPostResponse> response = postService.getPostByUser(id, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<GetPostResponse> getPostBySlug(
            @PathVariable String slug,
            @RequestParam(value = "lang", defaultValue = "vi") String lang,
            HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        GetPostResponse response = postService.getPostBySlug(slug);
        postViewService.incrementView(slug, ip);
        return ResponseEntity.ok(response);
    }
}
