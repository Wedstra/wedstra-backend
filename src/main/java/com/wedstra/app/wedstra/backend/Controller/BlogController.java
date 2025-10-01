package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.Blog;
import com.wedstra.app.wedstra.backend.Services.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @PostMapping("/create")
    public ResponseEntity<?> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            // Replace with actual logged-in user info
            String authorId = "exampleUserId";
            String authorType = "vendor"; // or "user"

            Blog savedBlog = blogService.createBlog(title, content, authorId, authorType, images);
            return ResponseEntity.ok(savedBlog);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create blog: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable String id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable String id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to create a blog with content file + optional images
    @PostMapping("/upload")
    public ResponseEntity<?> uploadBlog(
            @RequestParam("file") MultipartFile contentFile,         // PDF/Word file for content
            @RequestParam("title") String title,                     // Blog title
            @RequestParam("authorId") String authorId,              // Author ID
            @RequestParam("authorType") String authorType,          // "vendor" or "user"
            @RequestParam(value = "images", required = false) List<MultipartFile> images // Optional images
    ) {
        try {
            Blog savedBlog = blogService.createBlogWithFileAndImages(contentFile, title, authorId, authorType, images);
            return ResponseEntity.ok(savedBlog);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create blog: " + e.getMessage());
        }
    }

}
