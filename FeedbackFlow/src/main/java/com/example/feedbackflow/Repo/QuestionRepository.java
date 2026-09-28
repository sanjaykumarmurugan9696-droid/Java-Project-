package com.example.feedbackflow.Repo;

import com.example.feedbackflow.Model.FeedbackForm;
import com.example.feedbackflow.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByFeedbackForm(FeedbackForm feedbackForm);

    List<Question> findByFeedbackFormId(Long feedbackFormId);
}
