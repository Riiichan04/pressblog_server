package vn.id.devblog.blog_server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tag")
public class TagController {
    @GetMapping("/get")
    public ResponseEntity<Object> get() {
        return ResponseEntity.ok().build();
    }
}
