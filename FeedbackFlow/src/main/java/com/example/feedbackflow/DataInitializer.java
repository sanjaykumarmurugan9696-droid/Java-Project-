package com.example.feedbackflow;

import com.example.feedbackflow.Model.Course;
import com.example.feedbackflow.Model.FeedbackForm;
import com.example.feedbackflow.Model.Question;
import com.example.feedbackflow.Repo.CourseRepository;
import com.example.feedbackflow.Repo.FeedbackFormRepository;
import com.example.feedbackflow.Repo.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final FeedbackFormRepository feedbackFormRepository;
    private final QuestionRepository questionRepository;

    public DataInitializer(CourseRepository courseRepository,
                           FeedbackFormRepository feedbackFormRepository,
                           QuestionRepository questionRepository) {
        this.courseRepository = courseRepository;
        this.feedbackFormRepository = feedbackFormRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            // Course 1: Java Programming
            Course course1 = new Course("CS101", "Java Programming", "Dr. Kumar", "2026-1");
            course1 = courseRepository.save(course1);

            FeedbackForm form1 = new FeedbackForm(course1, true, LocalDate.of(2026, 10, 15));
            form1 = feedbackFormRepository.save(form1);

            questionRepository.save(new Question(form1, "How clearly does the faculty explain concepts?"));
            questionRepository.save(new Question(form1, "How effective are the practical sessions?"));
            questionRepository.save(new Question(form1, "How useful is the course material?"));

            // Course 2: Database Management Systems
            Course course2 = new Course("CS202", "Database Management Systems", "Prof. Sharma", "2026-1");
            course2 = courseRepository.save(course2);

            FeedbackForm form2 = new FeedbackForm(course2, true, LocalDate.of(2026, 10, 20));
            form2 = feedbackFormRepository.save(form2);

            questionRepository.save(new Question(form2, "How well are SQL and relational concepts explained?"));
            questionRepository.save(new Question(form2, "How helpful are the laboratory DBMS assignments?"));
            questionRepository.save(new Question(form2, "Are the study materials and notes helpful?"));
        }
    }
}
