# FeedbackFlow – Course Feedback Collection System

A clean, modern, and beginner-friendly Course Feedback Collection System built with **Java 17, Spring Boot, Spring Data JPA, MySQL**, and a responsive **HTML/CSS/Vanilla JavaScript** frontend.

---

## 📌 Project Overview

**FeedbackFlow** enables academic institutions and departments to systematically collect, validate, and analyze student feedback on courses and instructors:

1. **Course Management**: Faculty and administrators can add, edit, list, and delete courses.
2. **Feedback Form Setup**: Each course has an associated feedback form with customized rating questions and submission deadlines.
3. **Status Control**: Feedback forms can be opened or closed dynamically by administrators.
4. **Single-Submission Rule**: A student is strictly restricted to submitting feedback **only once** per course per semester.
5. **Rating Validation**: Validates ratings on a standard 1 to 5 scale (1 = Very Poor, 5 = Excellent).
6. **Analytics & Summaries**: Computes:
   - Question-wise average rating
   - Course-level overall average rating
   - Total number of student submissions
   - Faculty performance across all assigned courses

---

## 🛠️ Technology Stack

- **Backend**:
  - Java 17
  - Spring Boot 3.2.5
  - Spring Web (RESTful APIs)
  - Spring Data JPA & Hibernate
  - Bean Validation (`jakarta.validation`)
  - MySQL Connector/J
  - Springdoc OpenAPI (Swagger UI)
  - Apache Maven
- **Frontend**:
  - HTML5 & CSS3 (Modern, responsive, zero external UI frameworks)
  - Vanilla JavaScript with Fetch API & `async/await`
  - Served directly from Spring Boot: `src/main/resources/static/`
- **Database**:
  - MySQL Database: `feedbackflow`

---

## 🗄️ Database Design & Schema

The database model consists of four relational tables:

```
courses (1) ─────── (1) feedback_forms (1) ─────── (N) questions
   │
   └──────── (N) responses ─────── (1) questions
```

### 1. `courses` Table
- `id` (BIGINT, Primary Key, Auto Increment)
- `course_code` (VARCHAR, e.g., "CS101")
- `course_name` (VARCHAR, e.g., "Java Programming")
- `faculty_name` (VARCHAR, e.g., "Dr. Kumar")
- `semester` (VARCHAR, e.g., "2026-1")

### 2. `feedback_forms` Table
- `id` (BIGINT, Primary Key, Auto Increment)
- `course_id` (BIGINT, Foreign Key to `courses`, Unique)
- `is_open` (BOOLEAN, Form status: Open/Closed)
- `deadline` (DATE, e.g., "2026-10-15")

### 3. `questions` Table
- `id` (BIGINT, Primary Key, Auto Increment)
- `feedback_form_id` (BIGINT, Foreign Key to `feedback_forms`)
- `question_text` (VARCHAR(500), e.g., "How clearly does the faculty explain concepts?")

### 4. `responses` Table
- `id` (BIGINT, Primary Key, Auto Increment)
- `course_id` (BIGINT, Foreign Key to `courses`)
- `student_id` (VARCHAR, e.g., "STU001")
- `semester` (VARCHAR, e.g., "2026-1")
- `question_id` (BIGINT, Foreign Key to `questions`)
- `rating` (INT, 1 to 5)
- `submitted_at` (DATETIME)

---

## 🚀 Key Business Rules (Service Layer)

1. **One Submission Per Course Per Semester**:
   - Before saving feedback, the system executes `responseRepository.existsByCourseAndStudentIdAndSemester(course, studentId, semester)`.
   - If a record exists, HTTP `409 Conflict` with `DuplicateFeedbackException` is thrown.
2. **Rating Constraint (1 to 5)**:
   - Handled via `@Min(1)` and `@Max(5)` and service validation. Invalid values return HTTP `400 Bad Request`.
3. **Closed Form Constraint**:
   - If `!form.isOpen()`, rejects submission with `FeedbackClosedException` ("Feedback collection is closed.").
4. **Deadline Enforcement**:
   - If `LocalDate.now().isAfter(form.getDeadline())`, rejects submission with `FeedbackClosedException` ("Feedback deadline has passed.").
5. **Entity Integrity**:
   - A feedback form must belong to an existing course; questions must belong to an existing feedback form.

---

## 📡 REST API Reference

### 1. Courses (`/api/courses`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/courses` | Create new course | 201 Created |
| `GET` | `/api/courses` | List all courses | 200 OK |
| `GET` | `/api/courses/{id}` | Get course by ID | 200 OK |
| `PUT` | `/api/courses/{id}` | Update course | 200 OK |
| `DELETE`| `/api/courses/{id}` | Delete course & cascade | 204 No Content |

### 2. Feedback Forms (`/api/feedback-forms`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/feedback-forms` | Create/configure feedback form | 201 Created |
| `GET` | `/api/feedback-forms/{id}` | Get feedback form by ID | 200 OK |
| `GET` | `/api/feedback-forms/course/{courseId}` | Get feedback form by course ID | 200 OK |
| `PUT` | `/api/feedback-forms/{id}/open` | Open feedback collection | 200 OK |
| `PUT` | `/api/feedback-forms/{id}/close` | Close feedback collection | 200 OK |
| `POST` | `/api/feedback-forms/{formId}/questions` | Add question to form | 201 Created |
| `GET` | `/api/feedback-forms/{formId}/questions` | Get questions for form | 200 OK |

### 3. Feedback Submissions & Analytics (`/api/feedback`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/feedback` | Submit student ratings | 201 Created |
| `GET` | `/api/feedback/course/{courseId}/summary` | Course rating breakdown | 200 OK |
| `GET` | `/api/feedback/faculty/{facultyName}/summary` | Faculty courses analytics | 200 OK |

---

## 📋 Sample API Payloads

### Create Course (`POST /api/courses`)
```json
{
  "courseCode": "CS101",
  "courseName": "Java Programming",
  "facultyName": "Dr. Kumar",
  "semester": "2026-1"
}
```

### Submit Feedback (`POST /api/feedback`)
```json
{
  "courseId": 1,
  "studentId": "STU001",
  "ratings": [
    { "questionId": 1, "rating": 5 },
    { "questionId": 2, "rating": 4 },
    { "questionId": 3, "rating": 5 }
  ]
}
```

### Course Summary (`GET /api/feedback/course/1/summary`)
```json
{
  "courseName": "Java Programming",
  "facultyName": "Dr. Kumar",
  "totalResponses": 25,
  "overallAverage": 4.32,
  "questions": [
    {
      "question": "How clearly does the faculty explain concepts?",
      "averageRating": 4.5
    },
    {
      "question": "How useful is the course material?",
      "averageRating": 4.1
    }
  ]
}
```

---

## 🏃 Setup & Execution Instructions

### 1. Database Configuration
Ensure MySQL server is running. Create the database:
```sql
CREATE DATABASE feedbackflow;
```
Verify the credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/feedbackflow?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### 2. Run the Application
Navigate to the `FeedbackFlow` directory and execute:
```bash
mvn spring-boot:run
```

### 3. Access the Application
- **Web Dashboard**: [http://localhost:8080/](http://localhost:8080/)
- **Swagger UI (Interactive API Docs)**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🎓 Viva Questions & Answers

**Q1: How is the duplicate feedback submission prevented?**
> A composite check of `studentId`, `course`, and `semester` is executed in `ResponseRepository` using `existsByCourseAndStudentIdAndSemester`. If a record exists, a custom `DuplicateFeedbackException` is thrown and mapped to HTTP `409 Conflict` via `@RestControllerAdvice`.

**Q2: How are questions associated with feedback forms?**
> A `@ManyToOne` relationship links each `Question` entity to its parent `FeedbackForm`. Cascading operations and mapped references ensure clean JSON serialization and referential integrity.

**Q3: How are the overall and question-wise averages calculated?**
> In `FeedbackService.java`, responses for the course are fetched. For each question, matching ratings are aggregated and averaged to 2 decimal places. An overall average is computed across all responses.

**Q4: How does Spring Boot serve the frontend without a separate server?**
> Spring Boot automatically serves static assets (`index.html`, `style.css`, `script.js`) placed in `src/main/resources/static/` on the root URL `http://localhost:8080/`.
