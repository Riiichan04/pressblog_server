package vn.id.devblog.blog_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.devblog.blog_server.models.Category;
import vn.id.devblog.blog_server.models.Tag;
import vn.id.devblog.blog_server.services.PostMetadataService;

import java.util.List;

@RestController
@RequestMapping("/post/metadata")
@RequiredArgsConstructor
public class PostMedataController {
    private final PostMetadataService postMetadataService;

    @GetMapping("/category")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(postMetadataService.getAllCategories());
    }

    @GetMapping("/tag")
    public ResponseEntity<Page<Tag>> getAllTags(Pageable pageable) {
        return ResponseEntity.ok(postMetadataService.getAllTags(pageable));
    }
}
