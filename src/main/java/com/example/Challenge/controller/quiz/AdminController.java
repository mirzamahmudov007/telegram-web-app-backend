package com.example.Challenge.controller.quiz;

import com.example.Challenge.dto.TestCreateRequest;
import com.example.Challenge.dto.TestResponse;
import com.example.Challenge.model.Test;
import com.example.Challenge.model.User;
import com.example.Challenge.service.TestService;
import com.example.Challenge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Admin", description = "Admin boshqaruvi uchun API")
public class AdminController {

    private final TestService testService;
    private final UserService userService;

    @Operation(
            summary = "Barcha testlarni olish",
            description = "Admin uchun barcha testlarni olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Testlar muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = TestResponse.class))),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/tests")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<TestResponse>> getAllTests() {
        List<Test> tests = testService.getAllTests();
        return ResponseEntity.ok(testService.mapToTestResponseList(tests));
    }

    @Operation(
            summary = "Test ma'lumotlarini olish",
            description = "ID bo'yicha test ma'lumotlarini olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = Test.class))),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "404", description = "Test topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/tests/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Test> getTestById(@PathVariable Long testId) {
        return testService.getTestById(testId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Test yaratish",
            description = "Yangi test yaratish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test muvaffaqiyatli yaratildi",
                    content = @Content(schema = @Schema(implementation = Test.class))),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri ma'lumotlar"),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @PostMapping("/tests")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Test> createTest(@RequestBody TestCreateRequest request, @RequestParam Long userId) {
        Test test = testService.createTest(request, userId);
        return ResponseEntity.ok(test);
    }

    @Operation(
            summary = "Testni yangilash",
            description = "Mavjud testni yangilash"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test muvaffaqiyatli yangilandi",
                    content = @Content(schema = @Schema(implementation = Test.class))),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri ma'lumotlar"),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "404", description = "Test topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @PutMapping("/tests/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Test> updateTest(@PathVariable Long testId, @RequestBody TestCreateRequest request) {
        Test test = testService.updateTest(testId, request);
        return ResponseEntity.ok(test);
    }

    @Operation(
            summary = "Testni o'chirish",
            description = "Mavjud testni o'chirish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test muvaffaqiyatli o'chirildi"),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "404", description = "Test topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @DeleteMapping("/tests/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteTest(@PathVariable Long testId) {
        testService.deleteTest(testId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Testni faollashtirish",
            description = "Mavjud testni faol holatga o'tkazish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test muvaffaqiyatli faollashtirildi",
                    content = @Content(schema = @Schema(implementation = Test.class))),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "404", description = "Test topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @PostMapping("/tests/{testId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Test> activateTest(@PathVariable Long testId) {
        Test test = testService.activateTest(testId);
        return ResponseEntity.ok(test);
    }

    @Operation(
            summary = "Testni faolsizlashtirish",
            description = "Mavjud testni faol bo'lmagan holatga o'tkazish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test muvaffaqiyatli faolsizlashtirildi",
                    content = @Content(schema = @Schema(implementation = Test.class))),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "404", description = "Test topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @PostMapping("/tests/{testId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Test> deactivateTest(@PathVariable Long testId) {
        Test test = testService.deactivateTest(testId);
        return ResponseEntity.ok(test);
    }

    @Operation(
            summary = "Barcha foydalanuvchilarni olish",
            description = "Admin uchun barcha foydalanuvchilarni olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foydalanuvchilar muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Foydalanuvchi rolini o'zgartirish",
            description = "Foydalanuvchi rolini yangi rolga o'zgartirish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foydalanuvchi roli muvaffaqiyatli o'zgartirildi",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri rol yoki o'z-o'zini o'zgartirish urinishi"),
            @ApiResponse(responseCode = "403", description = "Ruxsat yo'q"),
            @ApiResponse(responseCode = "404", description = "Foydalanuvchi topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @PatchMapping("/users/{userId}/role/{role}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<User> changeUserRole(
            @PathVariable Long userId,
            @PathVariable String role,
            @RequestParam Long currentUserId) {

        // O'z-o'zini o'zgartirish urinishini tekshirish
        if (userId.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        // Rolni tekshirish
        if (!role.equals("USER") && !role.equals("ADMIN") && !role.equals("SUPERADMIN")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        User user = userService.changeUserRole(userId, role);
        return ResponseEntity.ok(user);
    }
}

