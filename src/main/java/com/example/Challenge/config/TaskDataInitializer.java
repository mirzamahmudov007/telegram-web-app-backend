package com.example.Challenge.config;

import com.example.Challenge.model.Task;
import com.example.Challenge.model.User;
import com.example.Challenge.repository.TaskRepository;
import com.example.Challenge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TaskDataInitializer {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Bean
    @Order(2)
    public CommandLineRunner initTaskData() {
        return args -> {
            // Only create sample tasks if there are none
            if (taskRepository.count() == 0) {
                Optional<User> adminUser = userService.getUserByUsername("superadmin");

                if (adminUser.isPresent()) {
                    User user = adminUser.get();
                    createSampleTasks(user);
                    log.info("Created sample tasks for user: {}", user.getUsername());
                }
            }
        };
    }

    private void createSampleTasks(User user) {
        List<Task> sampleTasks = Arrays.asList(
                createTask("Loyihani rejalashtirish", "Loyiha rejasini tuzish va vazifalarni belgilash",
                        Task.TaskStatus.COMPLETED, LocalDateTime.now().plusDays(5), 5, "Loyiha", true, user),
                createTask("Dizayn tayyorlash", "Mobil ilova uchun UI/UX dizaynini tayyorlash",
                        Task.TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(10), 4, "Dizayn", true, user),
                createTask("Backend API yaratish", "RESTful API endpointlarini ishlab chiqish",
                        Task.TaskStatus.CREATED, LocalDateTime.now().plusDays(15), 5, "Backend", false, user),
                createTask("Frontend ishlab chiqish", "React Native orqali mobil ilova frontendini yaratish",
                        Task.TaskStatus.CREATED, LocalDateTime.now().plusDays(20), 4, "Frontend", false, user),
                createTask("Testlash", "Ilovani testlash va xatolarni tuzatish",
                        Task.TaskStatus.CREATED, LocalDateTime.now().plusDays(25), 3, "QA", false, user),
                createTask("Hujjatlashtirish", "API va foydalanuvchi qo'llanmasini yaratish",
                        Task.TaskStatus.CREATED, LocalDateTime.now().plusDays(30), 2, "Hujjatlar", false, user),
                createTask("Deployment", "Ilovani production serveriga joylash",
                        Task.TaskStatus.CREATED, LocalDateTime.now().plusDays(35), 5, "DevOps", true, user),
                createTask("Marketing", "Ilova uchun marketing materiallarini tayyorlash",
                        Task.TaskStatus.CREATED, LocalDateTime.now().plusDays(40), 3, "Marketing", false, user),
                createTask("Analitika", "Analitika tizimini sozlash",
                        Task.TaskStatus.CREATED, LocalDateTime.now().plusDays(45), 2, "Analitika", false, user),
                createTask("Xavfsizlik tekshiruvi", "Ilova xavfsizligini tekshirish",
                        Task.TaskStatus.CREATED, LocalDateTime.now().plusDays(50), 5, "Xavfsizlik", true, user)
        );

        taskRepository.saveAll(sampleTasks);
    }

    private Task createTask(String title, String description, Task.TaskStatus status,
                            LocalDateTime dueDate, Integer priority, String category,
                            boolean isImportant, User user) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setCreatedAt(LocalDateTime.now());
        if (status == Task.TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now().minusDays(1));
        }
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setCategory(category);
        task.setImportant(isImportant);
        task.setUser(user);
        return task;
    }
}

