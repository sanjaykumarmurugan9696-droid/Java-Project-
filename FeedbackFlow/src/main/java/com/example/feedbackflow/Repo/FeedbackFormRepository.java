package com.example.feedbackflow.Repo;

import com.example.feedbackflow.Model.Course;
import com.example.feedbackflow.Model.FeedbackForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackFormRepository extends JpaRepository<FeedbackForm, Long> {

    Optional<FeedbackForm> findByCourse(Course course);

    Optional<FeedbackForm> findByCourseId(Long courseId);

    boolean existsByCourse(Course course);

    boolean existsByCourseId(Long courseId);
}
