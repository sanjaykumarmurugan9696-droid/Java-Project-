package com.example.feedbackflow.Service;

import com.example.feedbackflow.Dto.FacultySummaryResponse;
import com.example.feedbackflow.Dto.FeedbackSubmissionRequest;
import com.example.feedbackflow.Dto.FeedbackSummaryResponse;
import com.example.feedbackflow.Exception.CourseNotFoundException;
import com.example.feedbackflow.Exception.DuplicateFeedbackException;
import com.example.feedbackflow.Exception.FeedbackClosedException;
import com.example.feedbackflow.Exception.FeedbackFormNotFoundException;
import com.example.feedbackflow.Model.Course;
import com.example.feedbackflow.Model.FeedbackForm;
import com.example.feedbackflow.Model.Question;
import com.example.feedbackflow.Model.Response;
import com.example.feedbackflow.Repo.CourseRepository;
import com.example.feedbackflow.Repo.FeedbackFormRepository;
import com.example.feedbackflow.Repo.QuestionRepository;
import com.example.feedbackflow.Repo.ResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final CourseRepository courseRepository;
    private final FeedbackFormRepository feedbackFormRepository;
    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;

    public FeedbackService(CourseRepository courseRepository,
                           FeedbackFormRepository feedbackFormRepository,
                           QuestionRepository questionRepository,
                           ResponseRepository responseRepository) {
        this.courseRepository = courseRepository;
        this.feedbackFormRepository = feedbackFormRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
    }

    @Transactional
    public String submitFeedback(FeedbackSubmissionRequest request) {
        // Rule 5: Validate course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(request.getCourseId()));

        // Validate feedback form
        FeedbackForm feedbackForm = feedbackFormRepository.findByCourse(course)
                .orElseThrow(() -> new FeedbackFormNotFoundException("No feedback form configured for course: " + course.getCourseName()));

        // Rule 4: Feedback cannot be submitted if form is closed
        if (!feedbackForm.isOpen()) {
            throw new FeedbackClosedException("Feedback collection is closed.");
        }

        // Rule 3: Feedback cannot be submitted after deadline
        if (feedbackForm.getDeadline() != null && LocalDate.now().isAfter(feedbackForm.getDeadline())) {
            throw new FeedbackClosedException("Feedback deadline has passed.");
        }

        // Rule 1: Student can submit only once per course per semester
        String studentId = request.getStudentId().trim();
        String semester = course.getSemester();
        if (responseRepository.existsByCourseAndStudentIdAndSemester(course, studentId, semester)) {
            throw new DuplicateFeedbackException("Student has already submitted feedback for this course and semester.");
        }

        if (request.getRatings() == null || request.getRatings().isEmpty()) {
            throw new IllegalArgumentException("Feedback ratings cannot be empty.");
        }

        // Fetch valid questions for this form
        List<Question> formQuestions = questionRepository.findByFeedbackForm(feedbackForm);
        Map<Long, Question> questionMap = formQuestions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        List<Response> responsesToSave = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (FeedbackSubmissionRequest.RatingItem item : request.getRatings()) {
            // Rule 2: Rating must be 1 to 5
            if (item.getRating() == null || item.getRating() < 1 || item.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
            }

            // Rule 6: Question must belong to this feedback form
            Question question = questionMap.get(item.getQuestionId());
            if (question == null) {
                throw new IllegalArgumentException("Question id " + item.getQuestionId() + " does not belong to this course's feedback form.");
            }

            Response response = new Response(course, studentId, semester, question, item.getRating(), now);
            responsesToSave.add(response);
        }

        responseRepository.saveAll(responsesToSave);
        return "Feedback submitted successfully.";
    }

    public FeedbackSummaryResponse getCourseSummary(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        FeedbackForm feedbackForm = feedbackFormRepository.findByCourse(course)
                .orElse(null);

        List<Question> questions = (feedbackForm != null)
                ? questionRepository.findByFeedbackForm(feedbackForm)
                : Collections.emptyList();

        List<Response> courseResponses = responseRepository.findByCourse(course);

        // Calculate total distinct student submissions
        int totalResponses = (int) courseResponses.stream()
                .map(Response::getStudentId)
                .distinct()
                .count();

        // Calculate question-wise averages
        List<FeedbackSummaryResponse.QuestionSummary> questionSummaries = new ArrayList<>();
        for (Question q : questions) {
            List<Response> qResponses = courseResponses.stream()
                    .filter(r -> r.getQuestion().getId().equals(q.getId()))
                    .toList();

            double avgRating = 0.0;
            if (!qResponses.isEmpty()) {
                double sum = 0.0;
                for (Response r : qResponses) {
                    sum += r.getRating();
                }
                avgRating = Math.round((sum / qResponses.size()) * 100.0) / 100.0;
            }

            questionSummaries.add(new FeedbackSummaryResponse.QuestionSummary(q.getQuestionText(), avgRating));
        }

        // Calculate overall average across all question ratings
        double overallAverage = 0.0;
        if (!courseResponses.isEmpty()) {
            double totalSum = 0.0;
            for (Response r : courseResponses) {
                totalSum += r.getRating();
            }
            overallAverage = Math.round((totalSum / courseResponses.size()) * 100.0) / 100.0;
        }

        return new FeedbackSummaryResponse(
                course.getCourseName(),
                course.getFacultyName(),
                totalResponses,
                overallAverage,
                questionSummaries
        );
    }

    public List<FacultySummaryResponse> getFacultySummary(String facultyName) {
        String trimmedFaculty = facultyName.trim();
        List<Course> courses = courseRepository.findByFacultyNameIgnoreCase(trimmedFaculty);

        if (courses.isEmpty()) {
            courses = courseRepository.findByFacultyName(trimmedFaculty);
        }

        List<FacultySummaryResponse> result = new ArrayList<>();

        for (Course course : courses) {
            List<Response> responses = responseRepository.findByCourse(course);

            int totalStudents = (int) responses.stream()
                    .map(Response::getStudentId)
                    .distinct()
                    .count();

            double averageRating = 0.0;
            if (!responses.isEmpty()) {
                double sum = 0.0;
                for (Response r : responses) {
                    sum += r.getRating();
                }
                averageRating = Math.round((sum / responses.size()) * 100.0) / 100.0;
            }

            result.add(new FacultySummaryResponse(
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getSemester(),
                    totalStudents,
                    averageRating
            ));
        }

        return result;
    }
}
