package com.example.Challenge.service;

import com.example.Challenge.dto.*;
import com.example.Challenge.model.*;
import com.example.Challenge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserTestRepository userTestRepository;
    private final UserAnswerRepository userAnswerRepository;

    /**
     * Testni boshlash
     */
    @Transactional
    public UserTest startTest(Long userId, Long testId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        // Testning faolligini tekshirish
        LocalDateTime now = LocalDateTime.now();
        if (!test.getIsActive() || now.isBefore(test.getStartTime()) || now.isAfter(test.getEndTime())) {
            throw new RuntimeException("Test is not active at this time");
        }

        // Foydalanuvchining ushbu test uchun faol urinishi mavjudligini tekshirish
        Optional<UserTest> existingAttempt = userTestRepository.findActiveUserTest(user, test, now);
        if (existingAttempt.isPresent()) {
            return existingAttempt.get();
        }

        // Tugash vaqtini hisoblash (test tugash vaqti yoki hozir + davomiyligi, qaysi biri ertaroq bo'lsa)
        LocalDateTime expiresAt = now.plusMinutes(test.getDurationMinutes());
        if (expiresAt.isAfter(test.getEndTime())) {
            expiresAt = test.getEndTime();
        }

        // Yangi foydalanuvchi testini yaratish
        UserTest userTest = new UserTest();
        userTest.setUser(user);
        userTest.setTest(test);
        userTest.setStartedAt(now);
        userTest.setExpiresAt(expiresAt);
        userTest.setIsCompleted(false);

        // Maksimal ballni hisoblash
        int maxScore = test.getQuestions().stream()
                .mapToInt(Question::getPoints)
                .sum();
        userTest.setMaxScore(maxScore);

        return userTestRepository.save(userTest);
    }

    /**
     * Javobni yuborish
     */
    @Transactional
    public UserAnswer submitAnswer(Long userTestId, Long questionId, List<Long> optionIds) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new RuntimeException("User test not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Testning hali faolligini tekshirish
        LocalDateTime now = LocalDateTime.now();
        if (userTest.getIsCompleted() || now.isAfter(userTest.getExpiresAt())) {
            throw new RuntimeException("Test is already completed or expired");
        }

        // Savol testga tegishli ekanligini tekshirish
        if (!question.getTest().getId().equals(userTest.getTest().getId())) {
            throw new RuntimeException("Question does not belong to this test");
        }

        // Tanlangan variantlarni olish
        List<Option> selectedOptions = optionRepository.findAllById(optionIds);

        // Barcha tanlangan variantlar savolga tegishli ekanligini tekshirish
        for (Option option : selectedOptions) {
            if (!option.getQuestion().getId().equals(questionId)) {
                throw new RuntimeException("Option does not belong to this question");
            }
        }

        // Javob allaqachon mavjudligini tekshirish
        Optional<UserAnswer> existingAnswer = userAnswerRepository.findByUserTestAndQuestion(userTest, question);
        UserAnswer userAnswer;

        if (existingAnswer.isPresent()) {
            userAnswer = existingAnswer.get();
            userAnswer.getSelectedOptions().clear();
            userAnswer.getSelectedOptions().addAll(selectedOptions);
        } else {
            userAnswer = new UserAnswer();
            userAnswer.setUserTest(userTest);
            userAnswer.setQuestion(question);
            userAnswer.setSelectedOptions(selectedOptions);
        }

        // Javobning to'g'riligini tekshirish
        boolean isCorrect = false;
        int earnedPoints = 0;

        if (question.getType() == Question.QuestionType.SINGLE_CHOICE) {
            // Bitta to'g'ri javob uchun, aynan bitta to'g'ri variant bo'lishi kerak
            List<Option> correctOptions = optionRepository.findByQuestionAndIsCorrect(question, true);
            if (selectedOptions.size() == 1 && selectedOptions.get(0).getIsCorrect()) {
                isCorrect = true;
                earnedPoints = question.getPoints();
            }
        } else if (question.getType() == Question.QuestionType.MULTIPLE_CHOICE) {
            // Ko'p javobli savol uchun, barcha to'g'ri variantlar tanlangan va noto'g'ri variantlar tanlanmagan bo'lishi kerak
            List<Option> correctOptions = optionRepository.findByQuestionAndIsCorrect(question, true);
            List<Option> incorrectOptions = optionRepository.findByQuestionAndIsCorrect(question, false);

            boolean allCorrectSelected = selectedOptions.containsAll(correctOptions);
            boolean noIncorrectSelected = selectedOptions.stream().noneMatch(incorrectOptions::contains);

            if (allCorrectSelected && noIncorrectSelected) {
                isCorrect = true;
                earnedPoints = question.getPoints();
            }
        }

        userAnswer.setIsCorrect(isCorrect);
        userAnswer.setEarnedPoints(earnedPoints);

        return userAnswerRepository.save(userAnswer);
    }

    /**
     * Testni yakunlash
     */
    @Transactional
    public UserTest completeTest(Long userTestId) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new RuntimeException("User test not found"));

        // Test allaqachon yakunlanganligini tekshirish
        if (userTest.getIsCompleted()) {
            return userTest;
        }

        // Yakunlangan deb belgilash
        userTest.setIsCompleted(true);
        userTest.setFinishedAt(LocalDateTime.now());

        // Ballni hisoblash
        int score = userTest.getAnswers().stream()
                .mapToInt(UserAnswer::getEarnedPoints)
                .sum();
        userTest.setScore(score);

        return userTestRepository.save(userTest);
    }

    /**
     * Muddati o'tgan testlarni avtomatik yakunlash
     */
    @Transactional
    public void autoCompleteExpiredTests() {
        LocalDateTime now = LocalDateTime.now();
        List<UserTest> expiredTests = userTestRepository.findAll().stream()
                .filter(ut -> !ut.getIsCompleted() && now.isAfter(ut.getExpiresAt()))
                .collect(Collectors.toList());

        for (UserTest userTest : expiredTests) {
            completeTest(userTest.getId());
        }
    }

    /**
     * Keyingi savolni olish
     */
    public QuestionResponse getNextQuestion(Long userTestId) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new RuntimeException("User test not found"));

        // Testning hali faolligini tekshirish
        LocalDateTime now = LocalDateTime.now();
        if (userTest.getIsCompleted() || now.isAfter(userTest.getExpiresAt())) {
            throw new RuntimeException("Test is already completed or expired");
        }

        // Test uchun barcha savollarni olish
        List<Question> testQuestions = userTest.getTest().getQuestions();

        // Barcha javob berilgan savollarni olish
        List<Long> answeredQuestionIds = userTest.getAnswers().stream()
                .map(answer -> answer.getQuestion().getId())
                .collect(Collectors.toList());

        // Birinchi javob berilmagan savolni topish
        Optional<Question> nextQuestion = testQuestions.stream()
                .filter(q -> !answeredQuestionIds.contains(q.getId()))
                .findFirst();

        if (nextQuestion.isEmpty()) {
            return null; // Barcha savollarga javob berilgan
        }

        return mapToQuestionResponse(nextQuestion.get());
    }

    /**
     * Barcha savollarni olish
     */
    public List<QuestionResponse> getAllQuestions(Long userTestId) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new RuntimeException("User test not found"));

        return userTest.getTest().getQuestions().stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Test jarayonini olish
     */
    public TestProgressResponse getTestProgress(Long userTestId) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new RuntimeException("User test not found"));

        TestProgressResponse response = new TestProgressResponse();
        response.setUserTestId(userTest.getId());
        response.setTestId(userTest.getTest().getId());
        response.setTestTitle(userTest.getTest().getTitle());
        response.setStartedAt(userTest.getStartedAt());
        response.setExpiresAt(userTest.getExpiresAt());
        response.setIsCompleted(userTest.getIsCompleted());

        // Qolgan vaqtni sekundlarda hisoblash
        LocalDateTime now = LocalDateTime.now();
        long remainingSeconds = 0;
        if (!userTest.getIsCompleted() && now.isBefore(userTest.getExpiresAt())) {
            remainingSeconds = java.time.Duration.between(now, userTest.getExpiresAt()).getSeconds();
        }
        response.setRemainingSeconds(remainingSeconds);

        // Jarayonni hisoblash
        int totalQuestions = userTest.getTest().getQuestions().size();
        int answeredQuestions = userTest.getAnswers().size();
        response.setTotalQuestions(totalQuestions);
        response.setAnsweredQuestions(answeredQuestions);

        if (totalQuestions > 0) {
            response.setProgressPercentage((double) answeredQuestions / totalQuestions * 100);
        } else {
            response.setProgressPercentage(0.0);
        }

        return response;
    }

    /**
     * Test natijasini olish
     */
    public TestResultResponse getTestResult(Long userTestId) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new RuntimeException("User test not found"));

        // Muddati o'tgan bo'lsa avtomatik yakunlash
        LocalDateTime now = LocalDateTime.now();
        if (!userTest.getIsCompleted() && now.isAfter(userTest.getExpiresAt())) {
            userTest = completeTest(userTest.getId());
        }

        TestResultResponse response = new TestResultResponse();
        response.setUserTestId(userTest.getId());
        response.setTestId(userTest.getTest().getId());
        response.setTestTitle(userTest.getTest().getTitle());
        response.setStartedAt(userTest.getStartedAt());
        response.setFinishedAt(userTest.getFinishedAt());
        response.setScore(userTest.getScore());
        response.setMaxScore(userTest.getMaxScore());

        if (userTest.getMaxScore() > 0) {
            response.setScorePercentage((double) userTest.getScore() / userTest.getMaxScore() * 100);
        } else {
            response.setScorePercentage(0.0);
        }

        // Batafsil javoblarni olish
        List<QuestionResultResponse> questionResults = new ArrayList<>();
        for (Question question : userTest.getTest().getQuestions()) {
            QuestionResultResponse questionResult = new QuestionResultResponse();
            questionResult.setQuestionId(question.getId());
            questionResult.setQuestionText(question.getText());
            questionResult.setPoints(question.getPoints());

            // Foydalanuvchining ushbu savol uchun javobini olish
            Optional<UserAnswer> userAnswer = userTest.getAnswers().stream()
                    .filter(a -> a.getQuestion().getId().equals(question.getId()))
                    .findFirst();

            if (userAnswer.isPresent()) {
                questionResult.setIsAnswered(true);
                questionResult.setIsCorrect(userAnswer.get().getIsCorrect());
                questionResult.setEarnedPoints(userAnswer.get().getEarnedPoints());

                // Tanlangan variantlarni olish
                List<Long> selectedOptionIds = userAnswer.get().getSelectedOptions().stream()
                        .map(Option::getId)
                        .collect(Collectors.toList());
                questionResult.setSelectedOptionIds(selectedOptionIds);
            } else {
                questionResult.setIsAnswered(false);
                questionResult.setIsCorrect(false);
                questionResult.setEarnedPoints(0);
                questionResult.setSelectedOptionIds(new ArrayList<>());
            }

            // Barcha variantlarni to'g'ri javoblar bilan belgilangan holda olish
            List<OptionResultResponse> options = question.getOptions().stream()
                    .map(option -> {
                        OptionResultResponse optionResult = new OptionResultResponse();
                        optionResult.setOptionId(option.getId());
                        optionResult.setOptionText(option.getText());
                        optionResult.setIsCorrect(option.getIsCorrect());
                        return optionResult;
                    })
                    .collect(Collectors.toList());

            questionResult.setOptions(options);
            questionResults.add(questionResult);
        }

        response.setQuestionResults(questionResults);
        return response;
    }

    /**
     * Foydalanuvchi test tarixini olish
     */
    public List<TestResultResponse> getUserTestHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserTest> userTests = userTestRepository.findByUser(user);

        return userTests.stream()
                .filter(UserTest::getIsCompleted)
                .map(userTest -> getTestResult(userTest.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Savolni QuestionResponse formatiga o'tkazish
     */
    private QuestionResponse mapToQuestionResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setQuestionId(question.getId());
        response.setQuestionText(question.getText());
        response.setQuestionType(question.getType());
        response.setPoints(question.getPoints());

        // Variantlarni xaritalash (qaysi biri to'g'ri ekanligini oshkor qilmasdan)
        List<OptionResponse> options = question.getOptions().stream()
                .map(option -> {
                    OptionResponse optionResponse = new OptionResponse();
                    optionResponse.setOptionId(option.getId());
                    optionResponse.setOptionText(option.getText());
                    return optionResponse;
                })
                .collect(Collectors.toList());

        response.setOptions(options);
        return response;
    }
}

