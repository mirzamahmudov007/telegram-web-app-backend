package com.example.Challenge.config;

import com.example.Challenge.model.Permission;
import com.example.Challenge.model.User;
import com.example.Challenge.service.PermissionService;
import com.example.Challenge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;

    private final String superadminUsername = "superadmin";
    private final String superadminPassword = "superadmin";

    @Override
    public void run(String... args) {
        initializePermissions();
        createOrUpdateSuperAdmin();
    }

    private void initializePermissions() {
        List<Permission> defaultPermissions = Arrays.asList(
                // User permissions
                new Permission("user:read", "Read user information", "Permission to read user data"),
                new Permission("user:write", "Write user information", "Permission to create and update user data"),
                new Permission("user:delete", "Delete user", "Permission to delete users"),

                // Permission management
                new Permission("permission:read", "Read permissions", "Permission to read permission data"),
                new Permission("permission:write", "Write permissions", "Permission to create and update permissions"),
                new Permission("permission:delete", "Delete permissions", "Permission to delete permissions"),

                // Task permissions
                new Permission("task:read", "Read tasks", "Permission to read tasks"),
                new Permission("task:write", "Write tasks", "Permission to create and update tasks"),
                new Permission("task:delete", "Delete tasks", "Permission to delete tasks"),
                new Permission("task:stats", "View task statistics", "Permission to view task statistics")
        );

        for (Permission permission : defaultPermissions) {
            if (!permissionService.getPermissionById(permission.getId()).isPresent()) {
                permissionService.savePermission(permission);
                log.info("Created permission: {}", permission.getId());
            }
        }
    }

    private void createOrUpdateSuperAdmin() {
        if (!userService.existsByUsername(superadminUsername)) {
            log.info("Creating superadmin user");
            User superadmin = new User();
            superadmin.setUsername(superadminUsername);

            // Ensure password is properly encoded
            String encodedPassword = passwordEncoder.encode(superadminPassword);
            log.info("Encoded password for superadmin: {}", encodedPassword);
            superadmin.setPassword(encodedPassword);

            superadmin.setRole("SUPERADMIN");
            superadmin.setFirstName("Super");
            superadmin.setLastName("Admin");
            superadmin.setTelegramId("0");

            // Add all permissions to superadmin
            HashSet<String> allPermissions = new HashSet<>();
            permissionService.getAllPermissions().forEach(permission ->
                    allPermissions.add(permission.getId())
            );
            superadmin.setPermissions(allPermissions);

            User savedUser = userService.saveUser(superadmin);
            log.info("Superadmin user created successfully with ID: {}", savedUser.getId());
        } else {
            log.info("Superadmin user exists, updating password");
            Optional<User> superadminOpt = userService.getUserByUsername(superadminUsername);
            if (superadminOpt.isPresent()) {
                User superadmin = superadminOpt.get();
                // Update password
                superadmin.setPassword(passwordEncoder.encode(superadminPassword));
                // Ensure role is correct
                superadmin.setRole("SUPERADMIN");

                // Ensure all permissions are assigned
                HashSet<String> allPermissions = new HashSet<>();
                permissionService.getAllPermissions().forEach(permission ->
                        allPermissions.add(permission.getId())
                );
                superadmin.setPermissions(allPermissions);

                userService.saveUser(superadmin);
                log.info("Superadmin user updated successfully");
            }
        }
    }
}

