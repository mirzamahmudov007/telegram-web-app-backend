package com.example.Challenge.controller;

import com.example.Challenge.dto.LoginRequest;
import com.example.Challenge.dto.LoginResponse;
import com.example.Challenge.model.User;
import com.example.Challenge.security.JwtTokenProvider;
import com.example.Challenge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user with username and password and returns JWT tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful authentication",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied: Admin privileges required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for user: {}", loginRequest.getUsername());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            // Get user details
            Optional<User> userOpt = userService.getUserByUsername(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Check if user is an admin or superadmin
                if (!user.getRole().equalsIgnoreCase("ADMIN") &&
                        !user.getRole().equalsIgnoreCase("SUPERADMIN")) {
                    return ResponseEntity.status(403).body("Access denied: Admin privileges required");
                }

                // Create response
                LoginResponse response = new LoginResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRole(),
                        user.getPermissions(),
                        accessToken,
                        refreshToken
                );

                log.info("User logged in successfully: {}", user.getUsername());
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body("User not found");
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Refresh authentication token",
            description = "Uses a refresh token to generate a new access token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            // Remove "Bearer " prefix if present
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
            }

            // Validate refresh token
            if (jwtTokenProvider.validateToken(refreshToken)) {
                String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
                Optional<User> userOpt = userService.getUserByUsername(username);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // Create authentication object
                    Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

                    // Generate new tokens
                    String newAccessToken = jwtTokenProvider.generateToken(authentication);
                    String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

                    // Create response
                    LoginResponse response = new LoginResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getRole(),
                            user.getPermissions(),
                            newAccessToken,
                            newRefreshToken
                    );

                    return ResponseEntity.ok(response);
                }
            }

            return ResponseEntity.status(401).body("Invalid refresh token");
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Token refresh failed: " + e.getMessage());
        }
    }
}

