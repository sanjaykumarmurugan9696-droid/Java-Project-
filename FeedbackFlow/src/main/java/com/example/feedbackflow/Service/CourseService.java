package com.example.feedbackflow.Service;

import com.example.feedbackflow.Dto.CourseRequest;
import com.example.feedbackflow.Exception.CourseNotFoundException;
import com.example.feedbackflow.Model.Course;
import com.example.feedbackflow.Model.FeedbackForm;
import com.example.feedbackflow.Repo.CourseRepository;
import com.example.feedbackflow.Repo.FeedbackFormRepository;
import com.example.feedbackflow.Repo.ResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final FeedbackFormRepository feedbackFormRepository;
    private final ResponseRepository responseRepository;

    public CourseService(CourseRepository courseRepository,
                         FeedbackFormRepository feedbackFormRepository,
                         ResponseRepository responseRepository) {
        this.courseRepository = courseRepository;
        this.feedbackFormRepository = feedbackFormRepository;
        this.responseRepository = responseRepository;
    }

    public Course createCourse(CourseRequest request) {
        Course course = new Course(
                request.getCourseCode().trim(),
                request.getCourseName().trim(),
                request.getFacultyName().trim(),
                request.getSemester().trim()
        );
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public Course updateCourse(Long id, CourseRequest request) {
        Course course = getCourseById(id);
        course.setCourseCode(request.getCourseCode().trim());
        course.setCourseName(request.getCourseName().trim());
        course.setFacultyName(request.getFacultyName().trim());
        course.setSemester(request.getSemester().trim());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);

        // Delete associated responses if any
        responseRepository.deleteAll(responseRepository.findByCourse(course));

        // Delete associated feedback form if any
        feedbackFormRepository.findByCourse(course).ifPresent(feedbackFormRepository::delete);

        courseRepository.delete(course);
    }
}
