package com.example.feedbackflow.Controller;

import com.example.feedbackflow.Dto.FeedbackFormRequest;
import com.example.feedbackflow.Dto.QuestionRequest;
import com.example.feedbackflow.Model.FeedbackForm;
import com.example.feedbackflow.Model.Question;
import com.example.feedbackflow.Service.FeedbackFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback-forms")
@Tag(name = "Feedback Form Controller", description = "APIs for managing feedback forms and questions")
public class FeedbackFormController {

    private final FeedbackFormService feedbackFormService;

    public FeedbackFormController(FeedbackFormService feedbackFormService) {
        this.feedbackFormService = feedbackFormService;
    }

    @PostMapping
    @Operation(summary = "Create feedback form for a course")
    @ApiResponse(responseCode = "201", description = "Feedback form created")
    public ResponseEntity<FeedbackForm> createFeedbackForm(@Valid @RequestBody FeedbackFormRequest request) {
        FeedbackForm created = feedbackFormService.createFeedbackForm(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get feedback form by ID")
    @ApiResponse(responseCode = "200", description = "Feedback form retrieved")
    public ResponseEntity<FeedbackForm> getFeedbackFormById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackFormService.getFeedbackFormById(id));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get feedback form by course ID")
    @ApiResponse(responseCode = "200", description = "Feedback form retrieved for course")
    public ResponseEntity<FeedbackForm> getFeedbackFormByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(feedbackFormService.getFeedbackFormByCourseId(courseId));
    }

    @PutMapping("/{id}/open")
    @Operation(summary = "Open feedback collection for a form")
    @ApiResponse(responseCode = "200", description = "Feedback form opened")
    public ResponseEntity<FeedbackForm> openFeedbackForm(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackFormService.openFeedbackForm(id));
    }

    @PutMapping("/{id}/close")
    @Operation(summary = "Close feedback collection for a form")
    @ApiResponse(responseCode = "200", description = "Feedback form closed")
    public ResponseEntity<FeedbackForm> closeFeedbackForm(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackFormService.closeFeedbackForm(id));
    }

    @PostMapping("/{formId}/questions")
    @Operation(summary = "Add a question to a feedback form")
    @ApiResponse(responseCode = "201", description = "Question added successfully")
    public ResponseEntity<Question> addQuestion(@PathVariable Long formId, @Valid @RequestBody QuestionRequest request) {
        Question question = feedbackFormService.addQuestion(formId, request);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    @GetMapping("/{formId}/questions")
    @Operation(summary = "Get all questions for a feedback form")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<List<Question>> getQuestionsForForm(@PathVariable Long formId) {
        return ResponseEntity.ok(feedbackFormService.getQuestionsForForm(formId));
    }
}
