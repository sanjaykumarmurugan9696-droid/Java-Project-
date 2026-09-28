package com.example.feedbackflow.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class FeedbackSubmissionRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotEmpty(message = "Ratings list cannot be empty")
    @Valid
    private List<RatingItem> ratings;

    public FeedbackSubmissionRequest() {
    }

    public FeedbackSubmissionRequest(Long courseId, String studentId, List<RatingItem> ratings) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.ratings = ratings;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<RatingItem> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingItem> ratings) {
        this.ratings = ratings;
    }

    public static class RatingItem {

        @NotNull(message = "Question ID is required")
        private Long questionId;

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be between 1 and 5.")
        @Max(value = 5, message = "Rating must be between 1 and 5.")
        private Integer rating;

        public RatingItem() {
        }

        public RatingItem(Long questionId, Integer rating) {
            this.questionId = questionId;
            this.rating = rating;
        }

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }
}
