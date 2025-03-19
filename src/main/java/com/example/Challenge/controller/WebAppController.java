package com.example.Challenge.controller;

import com.example.Challenge.dto.TelegramUserInfoResponse;
import com.example.Challenge.model.User;
import com.example.Challenge.security.JwtTokenProvider;
import com.example.Challenge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/webapp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Web App", description = "Telegram Web App integration APIs")
public class WebAppController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "Authenticate Telegram Web App user",
            description = "Authenticates a user from Telegram Web App using userId and telegramId"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = TelegramUserInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID or Telegram ID")
    })
    @GetMapping("/auth")
    public ResponseEntity<TelegramUserInfoResponse> authenticateWebApp(
            @RequestParam("telegramId") String telegramId) {

        log.info("Web app authentication request for telegramId: {}", telegramId);

        // Foydalanuvchini faqat telegramId orqali tekshirish
        Optional<User> userOpt = userService.getUserByTelegramId(telegramId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Authentication obyektini yaratish
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
            user.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission))
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    new org.springframework.security.core.userdetails.User(
                            user.getUsername(), "", authorities
                    ),
                    null,
                    authorities
            );

            // Tokenlarni generatsiya qilish
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            // Foydalanuvchi ma'lumotlarini qaytarish
            TelegramUserInfoResponse response = new TelegramUserInfoResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getTelegramId(),
                    user.getRole(),
                    user.getPermissions(),
                    accessToken,
                    refreshToken
            );

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().build();
    }

}

