package com.example.feedbackflow.Exception;

public class FeedbackFormNotFoundException extends RuntimeException {

    public FeedbackFormNotFoundException(String message) {
        super(message);
    }

    public FeedbackFormNotFoundException(Long id) {
        super("Feedback form not found with id: " + id);
    }
}
