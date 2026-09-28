package com.example.feedbackflow.Repo;

import com.example.feedbackflow.Model.Course;
import com.example.feedbackflow.Model.Question;
import com.example.feedbackflow.Model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    boolean existsByCourseAndStudentIdAndSemester(Course course, String studentId, String semester);

    List<Response> findByCourse(Course course);

    List<Response> findByQuestion(Question question);

    List<Response> findByCourseId(Long courseId);

    List<Response> findByQuestionId(Long questionId);
}
