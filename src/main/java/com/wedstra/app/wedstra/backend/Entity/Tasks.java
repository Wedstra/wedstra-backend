package com.wedstra.app.wedstra.backend.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection="tasks")
public class Tasks {

    @Id
    private String id;
    private String title;
    private String type;
    private String createdBy;
    private String createAt;
    private String phase;
    private String task;

    public Tasks(){}

    public Tasks(String phase, String id, String title, String type, String createdBy, String createAt, String task) {
        this.phase = phase;
        this.id = id;
        this.title = title;
        this.type = type;
        this.createdBy = createdBy;
        this.createAt = createAt;
        this.task = task;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
