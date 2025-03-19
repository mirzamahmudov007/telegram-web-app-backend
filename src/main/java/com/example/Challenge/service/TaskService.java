package com.example.Challenge.service;

import com.example.Challenge.dto.TaskRequest;
import com.example.Challenge.dto.TaskResponse;
import com.example.Challenge.dto.TaskStatistics;
import com.example.Challenge.model.Task;
import com.example.Challenge.model.User;
import com.example.Challenge.repository.TaskRepository;
import com.example.Challenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUser(user).stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByUserAndStatus(Long userId, Task.TaskStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUserAndStatus(user, status).stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return TaskResponse.fromTask(task);
    }

    @Transactional
    public TaskResponse createTask(Long userId, TaskRequest taskRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus() != null ? taskRequest.getStatus() : Task.TaskStatus.CREATED);
        task.setDueDate(taskRequest.getDueDate());
        task.setPriority(taskRequest.getPriority());
        task.setCategory(taskRequest.getCategory());
        task.setImportant(taskRequest.isImportant());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        log.info("Created task with ID: {} for user: {}", savedTask.getId(), user.getUsername());

        return TaskResponse.fromTask(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setDueDate(taskRequest.getDueDate());
        task.setPriority(taskRequest.getPriority());
        task.setCategory(taskRequest.getCategory());
        task.setImportant(taskRequest.isImportant());

        Task updatedTask = taskRepository.save(task);
        log.info("Updated task with ID: {}", updatedTask.getId());

        return TaskResponse.fromTask(updatedTask);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, Task.TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);

        Task updatedTask = taskRepository.save(task);
        log.info("Updated task status to {} for task ID: {}", status, taskId);

        return TaskResponse.fromTask(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task not found");
        }

        taskRepository.deleteById(taskId);
        log.info("Deleted task with ID: {}", taskId);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUserAndDueDateBefore(user, LocalDateTime.now()).stream()
                .filter(task -> task.getStatus() != Task.TaskStatus.COMPLETED)
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getImportantTasks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUserAndIsImportant(user, true).stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByCategory(Long userId, String category) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUserAndCategory(user, category).stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Task> allTasks = taskRepository.findByUser(user);

        TaskStatistics statistics = new TaskStatistics();

        // Basic counts
        statistics.setTotalTasks((long) allTasks.size());
        statistics.setCompletedTasks(allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED).count());
        statistics.setInProgressTasks(allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS).count());
        statistics.setCreatedTasks(allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.CREATED).count());

        // Overdue tasks
        statistics.setOverdueTasks(allTasks.stream()
                .filter(t -> t.getStatus() != Task.TaskStatus.COMPLETED && t.getDueDate().isBefore(LocalDateTime.now()))
                .count());

        // Important tasks
        statistics.setImportantTasks(allTasks.stream().filter(Task::isImportant).count());

        // Average completion time
        Double avgCompletionTime = taskRepository.getAverageCompletionTimeInDays(user.getId());
        statistics.setAverageCompletionTimeInDays(avgCompletionTime != null ? avgCompletionTime : 0.0);

        // Tasks by category
        Map<String, Long> tasksByCategory = new HashMap<>();
        taskRepository.countTasksByCategory(user).forEach(result ->
                tasksByCategory.put((String) result[0], (Long) result[1]));
        statistics.setTasksByCategory(tasksByCategory);

        // Tasks by priority
        Map<Integer, Long> tasksByPriority = new HashMap<>();
        taskRepository.countTasksByPriority(user).forEach(result ->
                tasksByPriority.put((Integer) result[0], (Long) result[1]));
        statistics.setTasksByPriority(tasksByPriority);

        // Tasks by month for current year
        Map<Integer, Long> tasksByMonth = new HashMap<>();
        taskRepository.countTasksByMonthForCurrentYear(user).forEach(result ->
                tasksByMonth.put((Integer) result[0], (Long) result[1]));
        statistics.setTasksByMonth(tasksByMonth);

        // Top categories
        List<String> topCategories = tasksByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        statistics.setTopCategories(topCategories);

        // Completion rate
        double completionRate = statistics.getTotalTasks() > 0
                ? (double) statistics.getCompletedTasks() / statistics.getTotalTasks() * 100
                : 0;
        statistics.setCompletionRate(completionRate);

        return statistics;
    }
}

