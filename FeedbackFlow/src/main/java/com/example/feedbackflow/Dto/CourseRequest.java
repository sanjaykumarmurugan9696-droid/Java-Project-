package com.example.feedbackflow.Dto;

import jakarta.validation.constraints.NotBlank;

public class CourseRequest {

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotBlank(message = "Faculty name is required")
    private String facultyName;

    @NotBlank(message = "Semester is required")
    private String semester;

    public CourseRequest() {
    }

    public CourseRequest(String courseCode, String courseName, String facultyName, String semester) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.facultyName = facultyName;
        this.semester = semester;
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

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
