package com.example.Challenge.service;

import com.example.Challenge.dto.TestCreateRequest;
import com.example.Challenge.dto.TestResponse;
import com.example.Challenge.model.*;
import com.example.Challenge.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Challenge.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final UserService userService;

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public Optional<Test> getTestById(Long id) {
        return testRepository.findById(id);
    }

    public List<Test> getTestsByCreator(User creator) {
        return testRepository.findByCreatedBy(creator);
    }

    public List<Test> getActiveTests() {
        return testRepository.findActiveTests(LocalDateTime.now());
    }

    public List<Test> getActiveTestsBySubject(String subject) {
        return testRepository.findActiveTestsBySubject(LocalDateTime.now(), subject);
    }

    public List<String> getAllActiveSubjects() {
        return testRepository.findAllActiveSubjects();
    }

    @Transactional
    public Test createTest(TestCreateRequest request, Long creatorId) {
        User creator = userService.getUserById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Test test = new Test();
        test.setTitle(request.getTitle());
        test.setSubject(request.getSubject());
        test.setDescription(request.getDescription());
        test.setStartTime(request.getStartTime());
        test.setEndTime(request.getEndTime());
        test.setDurationMinutes(request.getDurationMinutes());
        test.setIsActive(request.getIsActive());
        test.setCreatedBy(creator);

        // Create questions and options
        request.getQuestions().forEach(questionRequest -> {
            Question question = new Question();
            question.setText(questionRequest.getText());
            question.setPoints(questionRequest.getPoints());
            question.setType(questionRequest.getType());

            // Create options
            questionRequest.getOptions().forEach(optionRequest -> {
                Option option = new Option();
                option.setText(optionRequest.getText());
                option.setIsCorrect(optionRequest.getIsCorrect());
                question.addOption(option);
            });

            test.addQuestion(question);
        });

        return testRepository.save(test);
    }

    @Transactional
    public Test updateTest(Long testId, TestCreateRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        test.setTitle(request.getTitle());
        test.setSubject(request.getSubject());
        test.setDescription(request.getDescription());
        test.setStartTime(request.getStartTime());
        test.setEndTime(request.getEndTime());
        test.setDurationMinutes(request.getDurationMinutes());
        test.setIsActive(request.getIsActive());

        // Clear existing questions and options
        test.getQuestions().clear();

        // Create new questions and options
        request.getQuestions().forEach(questionRequest -> {
            Question question = new Question();
            question.setText(questionRequest.getText());
            question.setPoints(questionRequest.getPoints());
            question.setType(questionRequest.getType());

            // Create options
            questionRequest.getOptions().forEach(optionRequest -> {
                Option option = new Option();
                option.setText(optionRequest.getText());
                option.setIsCorrect(optionRequest.getIsCorrect());
                question.addOption(option);
            });

            test.addQuestion(question);
        });

        return testRepository.save(test);
    }

    @Transactional
    public void deleteTest(Long testId) {
        testRepository.deleteById(testId);
    }

    @Transactional
    public Test activateTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        test.setIsActive(true);
        return testRepository.save(test);
    }

    @Transactional
    public Test deactivateTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        test.setIsActive(false);
        return testRepository.save(test);
    }

    public TestResponse mapToTestResponse(Test test) {
        TestResponse response = new TestResponse();
        response.setId(test.getId());
        response.setTitle(test.getTitle());
        response.setSubject(test.getSubject());
        response.setDescription(test.getDescription());
        response.setStartTime(test.getStartTime());
        response.setEndTime(test.getEndTime());
        response.setDurationMinutes(test.getDurationMinutes());
        response.setIsActive(test.getIsActive());
        response.setCreatedBy(test.getCreatedBy().getUsername());
        response.setQuestionCount(test.getQuestions().size());

        // Calculate total points
        int totalPoints = test.getQuestions().stream()
                .mapToInt(Question::getPoints)
                .sum();
        response.setTotalPoints(totalPoints);

        return response;
    }

    public List<TestResponse> mapToTestResponseList(List<Test> tests) {
        return tests.stream()
                .map(this::mapToTestResponse)
                .collect(Collectors.toList());
    }
}

