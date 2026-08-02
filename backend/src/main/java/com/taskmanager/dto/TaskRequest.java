package com.taskmanager.dto;


import com.taskmanager.entity.*;
import jakarta.validation.constraints.NotBlank;

public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    private String description;

    private TaskStatus status;

    private TaskPriority priority;
}
