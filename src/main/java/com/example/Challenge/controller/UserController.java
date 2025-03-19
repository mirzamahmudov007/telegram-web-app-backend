package com.example.Challenge.controller;

import com.example.Challenge.model.User;
import com.example.Challenge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Encode password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @Operation(
            summary = "Update an existing user",
            description = "Updates an existing user with the provided details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Get existing user to check if password is being updated
        User existingUser = userService.getUserById(id).get();

        // Set ID to ensure we're updating the correct user
        user.setId(id);

        // Only encode password if it's different from the stored one
        if (user.getPassword() != null && !user.getPassword().isEmpty()
                && !user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Keep existing password if not provided
            user.setPassword(existingUser.getPassword());
        }

        return ResponseEntity.ok(userService.saveUser(user));
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by their ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Add permission to user",
            description = "Adds a permission to a user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission added successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/{userId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> addPermissionToUser(
            @PathVariable Long userId,
            @PathVariable String permissionId) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.addPermissionToUser(userId, permissionId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Remove permission from user",
            description = "Removes a permission from a user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission removed successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{userId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> removePermissionFromUser(
            @PathVariable Long userId,
            @PathVariable String permissionId) {
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.removePermissionFromUser(userId, permissionId);
        return ResponseEntity.ok().build();
    }
}

