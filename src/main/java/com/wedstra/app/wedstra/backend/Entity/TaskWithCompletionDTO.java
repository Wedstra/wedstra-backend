package com.wedstra.app.wedstra.backend.Entity;

public class TaskWithCompletionDTO {
    private String id;
    private String title;
    private String type; // "predefined" or "custom"
    private boolean completed;


    public TaskWithCompletionDTO(String id, String title, String type, boolean completed) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
