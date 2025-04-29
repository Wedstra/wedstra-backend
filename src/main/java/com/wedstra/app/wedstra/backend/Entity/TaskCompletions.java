package com.wedstra.app.wedstra.backend.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="task-completions")
public class TaskCompletions {
    @Id
    private String id;
    private String userId;
    private String taskId;
    private boolean completed;
    private String completedAt;

    public TaskCompletions() {
    }

    public TaskCompletions(String userId, String taskId, boolean completed, String completedAt) {
        this.userId = userId;
        this.taskId = taskId;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}
