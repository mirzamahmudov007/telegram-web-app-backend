package com.example.Challenge.dto;

import com.example.Challenge.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime dueDate;
    private Integer priority;
    private String category;
    private boolean isImportant;
    private Long userId;
    private String username;

    public static TaskResponse fromTask(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setCompletedAt(task.getCompletedAt());
        response.setDueDate(task.getDueDate());
        response.setPriority(task.getPriority());
        response.setCategory(task.getCategory());
        response.setImportant(task.isImportant());
        response.setUserId(task.getUser().getId());
        response.setUsername(task.getUser().getUsername());
        return response;
    }
}

