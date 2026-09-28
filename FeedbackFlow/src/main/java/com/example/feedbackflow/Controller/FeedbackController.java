package com.example.feedbackflow.Controller;

import com.example.feedbackflow.Dto.FacultySummaryResponse;
import com.example.feedbackflow.Dto.FeedbackSubmissionRequest;
import com.example.feedbackflow.Dto.FeedbackSummaryResponse;
import com.example.feedbackflow.Service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@Tag(name = "Feedback Controller", description = "APIs for submitting feedback and viewing summaries")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    @Operation(summary = "Submit student feedback for a course")
    @ApiResponse(responseCode = "201", description = "Feedback submitted successfully")
    public ResponseEntity<Map<String, String>> submitFeedback(@Valid @RequestBody FeedbackSubmissionRequest request) {
        String message = feedbackService.submitFeedback(request);
        return new ResponseEntity<>(Collections.singletonMap("message", message), HttpStatus.CREATED);
    }

    @GetMapping("/course/{courseId}/summary")
    @Operation(summary = "Get feedback summary for a course")
    @ApiResponse(responseCode = "200", description = "Feedback summary retrieved")
    public ResponseEntity<FeedbackSummaryResponse> getCourseSummary(@PathVariable Long courseId) {
        return ResponseEntity.ok(feedbackService.getCourseSummary(courseId));
    }

    @GetMapping("/faculty/{facultyName}/summary")
    @Operation(summary = "Get feedback summary for all courses by a faculty member")
    @ApiResponse(responseCode = "200", description = "Faculty summary retrieved")
    public ResponseEntity<List<FacultySummaryResponse>> getFacultySummary(@PathVariable String facultyName) {
        return ResponseEntity.ok(feedbackService.getFacultySummary(facultyName));
    }
}
