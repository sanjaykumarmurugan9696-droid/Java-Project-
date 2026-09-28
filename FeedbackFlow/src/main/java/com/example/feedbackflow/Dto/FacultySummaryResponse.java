package com.example.feedbackflow.Dto;

public class FacultySummaryResponse {

    private String courseCode;
    private String courseName;
    private String semester;
    private int totalResponses;
    private double averageRating;

    public FacultySummaryResponse() {
    }

    public FacultySummaryResponse(String courseCode, String courseName, String semester, int totalResponses, double averageRating) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.semester = semester;
        this.totalResponses = totalResponses;
        this.averageRating = averageRating;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getTotalResponses() {
        return totalResponses;
    }

    public void setTotalResponses(int totalResponses) {
        this.totalResponses = totalResponses;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
