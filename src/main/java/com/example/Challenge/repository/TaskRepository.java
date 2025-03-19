package com.example.Challenge.repository;

import com.example.Challenge.model.Task;
import com.example.Challenge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(User user);

    List<Task> findByUserAndStatus(User user, Task.TaskStatus status);

    List<Task> findByUserAndDueDateBefore(User user, LocalDateTime date);

    List<Task> findByUserAndIsImportant(User user, boolean isImportant);

    List<Task> findByUserAndCategory(User user, String category);

    @Query("SELECT t.category, COUNT(t) FROM Task t WHERE t.user = ?1 GROUP BY t.category")
    List<Object[]> countTasksByCategory(User user);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.user = ?1 GROUP BY t.status")
    List<Object[]> countTasksByStatus(User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = ?1 AND t.completedAt BETWEEN ?2 AND ?3")
    Long countCompletedTasksBetweenDates(User user, LocalDateTime startDate, LocalDateTime endDate);

    // Completion time calculation using native query for better database compatibility
    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (t.completed_at - t.created_at))/86400) FROM tasks t WHERE t.user_id = ?1 AND t.status = 'COMPLETED'", nativeQuery = true)
    Double getAverageCompletionTimeInDays(Long userId);

    @Query("SELECT t.priority, COUNT(t) FROM Task t WHERE t.user = ?1 GROUP BY t.priority ORDER BY t.priority")
    List<Object[]> countTasksByPriority(User user);

    @Query("SELECT FUNCTION('MONTH', t.createdAt) as month, COUNT(t) FROM Task t WHERE t.user = ?1 AND FUNCTION('YEAR', t.createdAt) = FUNCTION('YEAR', CURRENT_DATE) GROUP BY FUNCTION('MONTH', t.createdAt) ORDER BY month")
    List<Object[]> countTasksByMonthForCurrentYear(User user);
}

