package com.example.feedbackflow.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class FeedbackFormRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Deadline is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    public FeedbackFormRequest() {
    }

    public FeedbackFormRequest(Long courseId, LocalDate deadline) {
        this.courseId = courseId;
        this.deadline = deadline;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
