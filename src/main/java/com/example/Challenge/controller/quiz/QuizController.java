package com.example.Challenge.controller.quiz;

import com.example.Challenge.dto.*;
import com.example.Challenge.model.Test;
import com.example.Challenge.model.User;
import com.example.Challenge.model.UserAnswer;
import com.example.Challenge.model.UserTest;
import com.example.Challenge.repository.UserRepository;
import com.example.Challenge.repository.UserTestRepository;
import com.example.Challenge.service.QuizService;
import com.example.Challenge.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Quiz", description = "Test va savollar bilan ishlash uchun API")
public class QuizController {

    private final TestService testService;
    private final QuizService quizService;
    private final UserRepository userRepository;
    private final UserTestRepository userTestRepository;

    @Operation(
            summary = "Faol testlarni olish",
            description = "Hozirda faol bo'lgan barcha testlarni olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Testlar muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = TestResponse.class))),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/tests")
    public ResponseEntity<List<TestResponse>> getActiveTests() {
        List<Test> tests = testService.getActiveTests();
        return ResponseEntity.ok(testService.mapToTestResponseList(tests));
    }

    @Operation(
            summary = "Fan bo'yicha faol testlarni olish",
            description = "Berilgan fan bo'yicha hozirda faol bo'lgan testlarni olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Testlar muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = TestResponse.class))),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/tests/subject/{subject}")
    public ResponseEntity<List<TestResponse>> getActiveTestsBySubject(@PathVariable String subject) {
        List<Test> tests = testService.getActiveTestsBySubject(subject);
        return ResponseEntity.ok(testService.mapToTestResponseList(tests));
    }

    @Operation(
            summary = "Faol fanlarni olish",
            description = "Hozirda faol testlari mavjud bo'lgan barcha fanlarni olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fanlar muvaffaqiyatli olindi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/subjects")
    public ResponseEntity<List<String>> getAllActiveSubjects() {
        List<String> subjects = testService.getAllActiveSubjects();
        return ResponseEntity.ok(subjects);
    }

    @Operation(
            summary = "Testni boshlash",
            description = "Foydalanuvchi uchun testni boshlash va urinish yaratish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test muvaffaqiyatli boshlandi",
                    content = @Content(schema = @Schema(implementation = UserTest.class))),
            @ApiResponse(responseCode = "400", description = "Test faol emas yoki noto'g'ri parametrlar"),
            @ApiResponse(responseCode = "404", description = "Test yoki foydalanuvchi topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @PostMapping("/tests/{testId}/start")
    public ResponseEntity<UserTestStartDTO> startTest(@PathVariable Long testId, @RequestParam Long userId) {
        UserTest userTest = quizService.startTest(userId, testId);
        return ResponseEntity.ok(UserTestStartDTO.fromEntity(userTest));
    }

    @Operation(
            summary = "Test jarayonini olish",
            description = "Foydalanuvchi testining joriy jarayoni haqida ma'lumot olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test jarayoni muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = TestProgressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Foydalanuvchi testi topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/tests/progress/{userTestId}")
    public ResponseEntity<TestProgressResponse> getTestProgress(@PathVariable Long userTestId) {
        TestProgressResponse progress = quizService.getTestProgress(userTestId);
        return ResponseEntity.ok(progress);
    }

    @Operation(
            summary = "Keyingi savolni olish",
            description = "Foydalanuvchi uchun keyingi javob berilmagan savolni olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Savol muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = QuestionResponse.class))),
            @ApiResponse(responseCode = "204", description = "Barcha savollarga javob berilgan"),
            @ApiResponse(responseCode = "404", description = "Foydalanuvchi testi topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/tests/{userTestId}/next-question")
    public ResponseEntity<QuestionResponse> getNextQuestion(@PathVariable Long userTestId) {
        QuestionResponse question = quizService.getNextQuestion(userTestId);
        if (question == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(question);
    }

    @Operation(
            summary = "Barcha savollarni olish",
            description = "Test uchun barcha savollarni olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Savollar muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = QuestionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Foydalanuvchi testi topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/tests/{userTestId}/all-questions")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions(@PathVariable Long userTestId) {
        List<QuestionResponse> questions = quizService.getAllQuestions(userTestId);
        return ResponseEntity.ok(questions);
    }

    @Operation(
            summary = "Javobni yuborish",
            description = "Savol uchun foydalanuvchi javobini yuborish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Javob muvaffaqiyatli yuborildi",
                    content = @Content(schema = @Schema(implementation = UserAnswer.class))),
            @ApiResponse(responseCode = "400", description = "Test yakunlangan yoki muddati o'tgan"),
            @ApiResponse(responseCode = "404", description = "Test yoki savol topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @PostMapping("/tests/{userTestId}/submit-answer")
    public ResponseEntity<UserAnswer> submitAnswer(
            @PathVariable Long userTestId,
            @RequestBody AnswerSubmitRequest request) {
        UserAnswer userAnswer = quizService.submitAnswer(
                userTestId,
                request.getQuestionId(),
                request.getOptionIds());
        return ResponseEntity.ok(userAnswer);
    }

    @Operation(
            summary = "Testni yakunlash",
            description = "Foydalanuvchi testini yakunlash va natijani hisoblash"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test muvaffaqiyatli yakunlandi",
                    content = @Content(schema = @Schema(implementation = UserTest.class))),
            @ApiResponse(responseCode = "404", description = "Foydalanuvchi testi topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @PostMapping("/tests/{userTestId}/complete")
    public ResponseEntity<UserTest> completeTest(@PathVariable Long userTestId) {
        UserTest userTest = quizService.completeTest(userTestId);
        return ResponseEntity.ok(userTest);
    }

    @Operation(
            summary = "Test natijasini olish",
            description = "Foydalanuvchi testining natijasini batafsil olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test natijasi muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = TestResultResponse.class))),
            @ApiResponse(responseCode = "404", description = "Foydalanuvchi testi topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/tests/{userTestId}/result")
    public ResponseEntity<TestResultResponse> getTestResult(@PathVariable Long userTestId) {
        TestResultResponse result = quizService.getTestResult(userTestId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Foydalanuvchi test tarixini olish",
            description = "Foydalanuvchining barcha yakunlangan testlari tarixini olish"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test tarixi muvaffaqiyatli olindi",
                    content = @Content(schema = @Schema(implementation = TestResultResponse.class))),
            @ApiResponse(responseCode = "404", description = "Foydalanuvchi topilmadi"),
            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
    })
    @GetMapping("/users/{userId}/history")
    public ResponseEntity<List<TestResultResponse>> getUserTestHistory(@PathVariable Long userId) {
        List<TestResultResponse> history = quizService.getUserTestHistory(userId);
        return ResponseEntity.ok(history);
    }
//
//    @Operation(
//            summary = "Foydalanuvchi test tarixini olish (DTO)",
//            description = "Foydalanuvchining barcha yakunlangan testlari tarixini DTO formatida olish"
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Test tarixi muvaffaqiyatli olindi"),
//            @ApiResponse(responseCode = "404", description = "Foydalanuvchi topilmadi"),
//            @ApiResponse(responseCode = "500", description = "Serverda xatolik yuz berdi")
//    })
//    @GetMapping("/users/{userId}/history-dto")
//    public ResponseEntity<List<UserTestDTO>> getUserTestHistoryDTO(@PathVariable Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<UserTest> userTests = userTestRepository.findByUser(user);
//
//        List<UserTestDTO> userTestDTOs = userTests.stream()
//                .filter(UserTest::getIsCompleted)
//                .map(UserTestDTO::fromEntity)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(userTestDTOs);
//    }
}

