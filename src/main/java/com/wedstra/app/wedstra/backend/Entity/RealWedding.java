package com.wedstra.app.wedstra.backend.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "real_weddings")
public class RealWedding {

    @Id
    private String id;
    private String title;
    private List<String> fileUrls;
    private LocalDateTime createdAt;  // New field

    public RealWedding() {
        this.createdAt = LocalDateTime.now(); // Auto-set on default constructor
    }

    public RealWedding(String title, List<String> fileUrls) {
        this.title = title;
        this.fileUrls = fileUrls;
        this.createdAt = LocalDateTime.now(); // Auto-set when object is created
    }

    public RealWedding(String title) {
        this.title = title;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getFileUrls() { return fileUrls; }
    public void setFileUrls(List<String> fileUrls) { this.fileUrls = fileUrls; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

