package com.example.Challenge.controller;

import com.example.Challenge.dto.TaskRequest;
import com.example.Challenge.dto.TaskResponse;
import com.example.Challenge.dto.TaskStatistics;
import com.example.Challenge.model.Task;
import com.example.Challenge.model.User;
import com.example.Challenge.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Get all tasks for a user",
            description = "Retrieves all tasks for the specified user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('task:read') and (authentication.principal.username == @userService.getUserById(#userId).orElse(new com.example.Challenge.model.User()).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<List<TaskResponse>> getAllTasksByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getAllTasksByUser(userId));
    }

    @Operation(
            summary = "Get tasks by status for a user",
            description = "Retrieves tasks with the specified status for the user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/status/{status}")
    @PreAuthorize("hasAuthority('task:read') and (authentication.principal.username == @userService.getUserById(#userId).orElse(new com.example.Challenge.model.User()).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<List<TaskResponse>> getTasksByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByUserAndStatus(userId, status));
    }

    @Operation(
            summary = "Get a task by ID",
            description = "Retrieves a task by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved task"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{taskId}")
    @PreAuthorize("hasAuthority('task:read') and (authentication.principal.username == @taskService.getTaskById(#taskId).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @Operation(
            summary = "Create a new task",
            description = "Creates a new task for the specified user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('task:write') and (authentication.principal.username == @userService.getUserById(#userId).orElse(new com.example.Challenge.model.User()).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long userId,
            @Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.createTask(userId, taskRequest));
    }

    @Operation(
            summary = "Update a task",
            description = "Updates an existing task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{taskId}")
    @PreAuthorize("hasAuthority('task:write') and (authentication.principal.username == @taskService.getTaskById(#taskId).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskRequest));
    }

    @Operation(
            summary = "Update task status",
            description = "Updates the status of an existing task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PatchMapping("/{taskId}/status/{status}")
    @PreAuthorize("hasAuthority('task:write') and (authentication.principal.username == @taskService.getTaskById(#taskId).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }

    @Operation(
            summary = "Delete a task",
            description = "Deletes an existing task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAuthority('task:delete') and (authentication.principal.username == @taskService.getTaskById(#taskId).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get overdue tasks",
            description = "Retrieves all overdue tasks for the specified user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/overdue")
    @PreAuthorize("hasAuthority('task:read') and (authentication.principal.username == @userService.getUserById(#userId).orElse(new com.example.Challenge.model.User()).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getOverdueTasks(userId));
    }

    @Operation(
            summary = "Get important tasks",
            description = "Retrieves all important tasks for the specified user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/important")
    @PreAuthorize("hasAuthority('task:read') and (authentication.principal.username == @userService.getUserById(#userId).orElse(new com.example.Challenge.model.User()).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<List<TaskResponse>> getImportantTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getImportantTasks(userId));
    }

    @Operation(
            summary = "Get tasks by category",
            description = "Retrieves all tasks in the specified category for the user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/category/{category}")
    @PreAuthorize("hasAuthority('task:read') and (authentication.principal.username == @userService.getUserById(#userId).orElse(new com.example.Challenge.model.User()).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<List<TaskResponse>> getTasksByCategory(
            @PathVariable Long userId,
            @PathVariable String category) {
        return ResponseEntity.ok(taskService.getTasksByCategory(userId, category));
    }

    @Operation(
            summary = "Get task statistics",
            description = "Retrieves task statistics for the specified user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/statistics")
    @PreAuthorize("hasAuthority('task:stats') and (authentication.principal.username == @userService.getUserById(#userId).orElse(new com.example.Challenge.model.User()).username or hasRole('ADMIN') or hasRole('SUPERADMIN'))")
    public ResponseEntity<TaskStatistics> getTaskStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTaskStatistics(userId));
    }
}