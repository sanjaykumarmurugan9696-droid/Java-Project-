package com.example.feedbackflow.Dto;

import java.util.ArrayList;
import java.util.List;

public class FeedbackSummaryResponse {

    private String courseName;
    private String facultyName;
    private int totalResponses;
    private double overallAverage;
    private List<QuestionSummary> questions = new ArrayList<>();

    public FeedbackSummaryResponse() {
    }

    public FeedbackSummaryResponse(String courseName, String facultyName, int totalResponses, double overallAverage, List<QuestionSummary> questions) {
        this.courseName = courseName;
        this.facultyName = facultyName;
        this.totalResponses = totalResponses;
        this.overallAverage = overallAverage;
        this.questions = questions;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public int getTotalResponses() {
        return totalResponses;
    }

    public void setTotalResponses(int totalResponses) {
        this.totalResponses = totalResponses;
    }

    public double getOverallAverage() {
        return overallAverage;
    }

    public void setOverallAverage(double overallAverage) {
        this.overallAverage = overallAverage;
    }

    public List<QuestionSummary> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionSummary> questions) {
        this.questions = questions;
    }

    public static class QuestionSummary {
        private String question;
        private double averageRating;

        public QuestionSummary() {
        }

        public QuestionSummary(String question, double averageRating) {
            this.question = question;
            this.averageRating = averageRating;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public double getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(double averageRating) {
            this.averageRating = averageRating;
        }
    }
}
