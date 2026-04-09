package vn.id.devblog.blog_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.devblog.blog_server.dto.request.post.PostRequest;
import vn.id.devblog.blog_server.dto.response.post.GetPostResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.services.PostService;

@RestController
@RequestMapping("/post")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/upload")
    public ResponseEntity<PostResponse> uploadNewPost(@RequestBody PostRequest postRequest) {
        PostResponse response = postService.insertNewPost(postRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<PostResponse> editPost(@RequestParam Long id, @RequestBody PostRequest postRequest) {
        PostResponse response = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<PostResponse> deletePost(@RequestBody Long id) {
        PostResponse response = postService.deletePost(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{id}")
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
            @RequestParam(value = "lang", defaultValue = "vi") String lang
    ) {
        GetPostResponse response = postService.getPostBySlug(slug);
        return ResponseEntity.ok(response);
    }
}
