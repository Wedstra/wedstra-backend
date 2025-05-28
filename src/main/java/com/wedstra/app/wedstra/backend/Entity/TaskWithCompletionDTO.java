package com.wedstra.app.wedstra.backend.Entity;

public class TaskWithCompletionDTO {
    private String id;
    private String title;
    private String type;
    private String phase;
    private String task;
    private boolean completed;


    public TaskWithCompletionDTO(String id, String title, String type, String phase, String task, boolean completed) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.phase = phase;
        this.task = task;
        this.completed = completed;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
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
