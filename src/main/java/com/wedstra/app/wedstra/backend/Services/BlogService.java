package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.Blog;
import com.wedstra.app.wedstra.backend.Repo.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public Blog createBlog(Blog blog) {
        blog.setCreatedAt(java.time.LocalDateTime.now());
        return blogRepository.save(blog);
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Optional<Blog> getBlogById(String id) {
        return blogRepository.findById(id);
    }

    public void deleteBlog(String id) {
        blogRepository.deleteById(id);
    }
}
