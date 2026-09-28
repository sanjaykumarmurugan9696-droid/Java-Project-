package com.example.feedbackflow.Dto;

import jakarta.validation.constraints.NotBlank;

public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    private String questionText;

    public QuestionRequest() {
    }

    public QuestionRequest(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
