package com.example.Challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatistics {

    private Long totalTasks;
    private Long completedTasks;
    private Long inProgressTasks;
    private Long createdTasks;
    private Long overdueTasks;
    private Long importantTasks;
    private Double averageCompletionTimeInDays;
    private Map<String, Long> tasksByCategory;
    private Map<Integer, Long> tasksByPriority;
    private Map<Integer, Long> tasksByMonth;
    private List<String> topCategories;
    private Double completionRate;
}

