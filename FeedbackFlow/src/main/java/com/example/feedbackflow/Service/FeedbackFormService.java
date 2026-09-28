package com.example.feedbackflow.Service;

import com.example.feedbackflow.Dto.FeedbackFormRequest;
import com.example.feedbackflow.Dto.QuestionRequest;
import com.example.feedbackflow.Exception.CourseNotFoundException;
import com.example.feedbackflow.Exception.FeedbackFormNotFoundException;
import com.example.feedbackflow.Model.Course;
import com.example.feedbackflow.Model.FeedbackForm;
import com.example.feedbackflow.Model.Question;
import com.example.feedbackflow.Repo.CourseRepository;
import com.example.feedbackflow.Repo.FeedbackFormRepository;
import com.example.feedbackflow.Repo.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackFormService {

    private final FeedbackFormRepository feedbackFormRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;

    public FeedbackFormService(FeedbackFormRepository feedbackFormRepository,
                               CourseRepository courseRepository,
                               QuestionRepository questionRepository) {
        this.feedbackFormRepository = feedbackFormRepository;
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
    }

    public FeedbackForm createFeedbackForm(FeedbackFormRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(request.getCourseId()));

        // If form already exists for this course, update deadline and reopen
        FeedbackForm feedbackForm = feedbackFormRepository.findByCourse(course)
                .orElse(new FeedbackForm());

        feedbackForm.setCourse(course);
        feedbackForm.setDeadline(request.getDeadline());
        feedbackForm.setOpen(true);

        return feedbackFormRepository.save(feedbackForm);
    }

    public FeedbackForm getFeedbackFormById(Long id) {
        return feedbackFormRepository.findById(id)
                .orElseThrow(() -> new FeedbackFormNotFoundException(id));
    }

    public FeedbackForm getFeedbackFormByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        return feedbackFormRepository.findByCourse(course)
                .orElseThrow(() -> new FeedbackFormNotFoundException("No feedback form found for course: " + course.getCourseName()));
    }

    public FeedbackForm openFeedbackForm(Long id) {
        FeedbackForm form = getFeedbackFormById(id);
        form.setOpen(true);
        return feedbackFormRepository.save(form);
    }

    public FeedbackForm closeFeedbackForm(Long id) {
        FeedbackForm form = getFeedbackFormById(id);
        form.setOpen(false);
        return feedbackFormRepository.save(form);
    }

    @Transactional
    public Question addQuestion(Long formId, QuestionRequest request) {
        FeedbackForm form = getFeedbackFormById(formId);
        Question question = new Question(form, request.getQuestionText().trim());
        return questionRepository.save(question);
    }

    public List<Question> getQuestionsForForm(Long formId) {
        FeedbackForm form = getFeedbackFormById(formId);
        return questionRepository.findByFeedbackForm(form);
    }
}
