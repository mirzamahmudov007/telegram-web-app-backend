package com.example.Challenge.controller;

import com.example.Challenge.model.Permission;
import com.example.Challenge.service.PermissionService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permission management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(
            summary = "Get all permissions",
            description = "Retrieves a list of all permissions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved permissions"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @Operation(
            summary = "Get permission by ID",
            description = "Retrieves a permission by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved permission"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<Permission> getPermissionById(@PathVariable String id) {
        Optional<Permission> permission = permissionService.getPermissionById(id);
        return permission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create a new permission",
            description = "Creates a new permission with the provided details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('permission:write')")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(permissionService.savePermission(permission));
    }

    @Operation(
            summary = "Update an existing permission",
            description = "Updates an existing permission with the provided details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission updated successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:write')")
    public ResponseEntity<Permission> updatePermission(@PathVariable String id, @RequestBody Permission permission) {
        if (!permissionService.getPermissionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        permission.setId(id);
        return ResponseEntity.ok(permissionService.savePermission(permission));
    }

    @Operation(
            summary = "Delete a permission",
            description = "Deletes a permission by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ResponseEntity<Void> deletePermission(@PathVariable String id) {
        if (!permissionService.getPermissionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        permissionService.deletePermission(id);
        return ResponseEntity.ok().build();
    }
}

