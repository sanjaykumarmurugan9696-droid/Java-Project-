/**
 * FeedbackFlow – Course Feedback Collection System
 * Frontend Controller using Fetch API & Modern JavaScript
 */

const API_BASE = "/api";

// Global in-memory state
let currentSelectedCourseId = null;
let currentFeedbackForm = null;

// ==========================================================================
// Initialization
// ==========================================================================
document.addEventListener("DOMContentLoaded", () => {
    loadCourses();

    // Default deadline to 30 days ahead for convenience
    const defaultDate = new Date();
    defaultDate.setDate(defaultDate.getDate() + 30);
    const deadlineInput = document.getElementById("formDeadline");
    if (deadlineInput) {
        deadlineInput.value = defaultDate.toISOString().split("T")[0];
    }
});

// ==========================================================================
// Alert / Notification System
// ==========================================================================
let alertTimeout = null;

function showAlert(message, type = "success") {
    const alertBox = document.getElementById("alert-box");
    const alertMsg = document.getElementById("alert-message");
    const alertIcon = document.getElementById("alert-icon");

    if (!alertBox || !alertMsg) return;

    alertBox.className = "alert-box";
    if (type === "success") {
        alertBox.classList.add("alert-success");
        alertIcon.textContent = "✓";
    } else if (type === "error") {
        alertBox.classList.add("alert-error");
        alertIcon.textContent = "✕";
    } else {
        alertBox.classList.add("alert-info");
        alertIcon.textContent = "ℹ";
    }

    alertMsg.textContent = message;
    alertBox.classList.remove("hidden");

    if (alertTimeout) clearTimeout(alertTimeout);
    alertTimeout = setTimeout(() => {
        closeAlert();
    }, 4500);
}

function closeAlert() {
    const alertBox = document.getElementById("alert-box");
    if (alertBox) {
        alertBox.classList.add("hidden");
    }
}

// ==========================================================================
// Tab Navigation
// ==========================================================================
function switchTab(targetTabId) {
    // Hide all tab contents
    document.querySelectorAll(".tab-content").forEach(el => {
        el.classList.remove("active");
    });

    // Deactivate all nav buttons
    document.querySelectorAll(".nav-btn").forEach(btn => {
        btn.classList.remove("active");
    });

    // Activate target section
    const targetSection = document.getElementById(targetTabId);
    if (targetSection) {
        targetSection.classList.add("active");
    }

    // Activate corresponding nav button
    const activeBtn = document.querySelector(`.nav-btn[data-tab="${targetTabId}"]`);
    if (activeBtn) {
        activeBtn.classList.add("active");
    }

    // Tab-specific refreshes
    if (targetTabId === "courses-section") {
        loadCourses();
    } else if (targetTabId === "feedback-forms-section") {
        loadCoursesDropdowns();
    } else if (targetTabId === "submit-feedback-section") {
        loadCoursesDropdowns();
    } else if (targetTabId === "course-summary-section") {
        loadCoursesDropdowns();
    }
}

// ==========================================================================
// 1. Course Management
// ==========================================================================
async function loadCourses() {
    const grid = document.getElementById("courses-grid");
    const emptyState = document.getElementById("courses-empty");
    const loading = document.getElementById("courses-loading");

    if (loading) loading.classList.remove("hidden");

    try {
        const response = await fetch(`${API_BASE}/courses`);
        if (!response.ok) {
            throw new Error(`Failed to load courses (HTTP ${response.status})`);
        }
        const courses = await response.json();

        if (loading) loading.classList.add("hidden");

        if (!courses || courses.length === 0) {
            grid.innerHTML = "";
            emptyState.classList.remove("hidden");
            return;
        }

        emptyState.classList.add("hidden");
        grid.innerHTML = courses.map(course => `
            <div class="course-card" id="course-card-${course.id}">
                <div>
                    <div class="course-card-header">
                        <span class="badge badge-code">${escapeHtml(course.courseCode)}</span>
                        <span class="badge badge-closed">${escapeHtml(course.semester)}</span>
                    </div>
                    <h3 class="course-card-title">${escapeHtml(course.courseName)}</h3>
                    <p class="course-meta"><strong>Faculty:</strong> ${escapeHtml(course.facultyName)}</p>
                    <p class="course-meta"><strong>Semester:</strong> ${escapeHtml(course.semester)}</p>
                </div>
                <div class="course-card-actions">
                    <button class="btn btn-secondary btn-sm" onclick="editCourse(${course.id})">Edit</button>
                    <button class="btn btn-outline-danger btn-sm" onclick="deleteCourse(${course.id})">Delete</button>
                    <button class="btn btn-primary btn-sm" onclick="openFeedbackFormTabForCourse(${course.id})">Feedback Form</button>
                    <button class="btn btn-secondary btn-sm" onclick="openSummaryTabForCourse(${course.id})">Summary</button>
                </div>
            </div>
        `).join("");

        // Also update all select dropdowns
        updateAllCourseDropdowns(courses);

    } catch (err) {
        if (loading) loading.classList.add("hidden");
        showAlert(err.message, "error");
    }
}

async function handleCreateCourse(e) {
    e.preventDefault();
    const form = document.getElementById("create-course-form");
    const submitBtn = document.getElementById("btn-save-course");

    const payload = {
        courseCode: document.getElementById("courseCode").value.trim(),
        courseName: document.getElementById("courseName").value.trim(),
        facultyName: document.getElementById("facultyName").value.trim(),
        semester: document.getElementById("semester").value.trim()
    };

    submitBtn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/courses`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Failed to create course.");
        }

        showAlert("Course created successfully.", "success");
        form.reset();
        switchTab("courses-section");
    } catch (err) {
        showAlert(err.message, "error");
    } finally {
        submitBtn.disabled = false;
    }
}

async function editCourse(id) {
    try {
        const response = await fetch(`${API_BASE}/courses/${id}`);
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || "Course not found.");
        }
        const course = await response.json();

        document.getElementById("edit-course-id").value = course.id;
        document.getElementById("edit-course-code").value = course.courseCode;
        document.getElementById("edit-course-name").value = course.courseName;
        document.getElementById("edit-faculty-name").value = course.facultyName;
        document.getElementById("edit-semester").value = course.semester;

        document.getElementById("edit-course-modal").classList.remove("hidden");
    } catch (err) {
        showAlert(err.message, "error");
    }
}

function closeEditModal() {
    document.getElementById("edit-course-modal").classList.add("hidden");
}

async function handleUpdateCourse(e) {
    e.preventDefault();
    const id = document.getElementById("edit-course-id").value;
    const payload = {
        courseCode: document.getElementById("edit-course-code").value.trim(),
        courseName: document.getElementById("edit-course-name").value.trim(),
        facultyName: document.getElementById("edit-faculty-name").value.trim(),
        semester: document.getElementById("edit-semester").value.trim()
    };

    try {
        const response = await fetch(`${API_BASE}/courses/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Failed to update course.");
        }

        closeEditModal();
        showAlert("Course updated successfully.", "success");
        loadCourses();
    } catch (err) {
        showAlert(err.message, "error");
    }
}

async function deleteCourse(id) {
    if (!confirm("Are you sure you want to delete this course?")) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/courses/${id}`, {
            method: "DELETE"
        });

        if (!response.ok && response.status !== 204) {
            const data = await response.json().catch(() => ({}));
            throw new Error(data.message || "Failed to delete course.");
        }

        showAlert("Course deleted successfully.", "success");
        loadCourses();
    } catch (err) {
        showAlert(err.message, "error");
    }
}

// Helpers for dropdowns
async function loadCoursesDropdowns() {
    try {
        const response = await fetch(`${API_BASE}/courses`);
        if (response.ok) {
            const courses = await response.json();
            updateAllCourseDropdowns(courses);
        }
    } catch (e) {
        console.error("Failed to load courses for dropdowns", e);
    }
}

function updateAllCourseDropdowns(courses) {
    const dropdownIds = [
        "form-course-select",
        "feedback-course-select",
        "summary-course-select"
    ];

    dropdownIds.forEach(selectId => {
        const select = document.getElementById(selectId);
        if (!select) return;

        const currentVal = select.value;
        select.innerHTML = '<option value="">-- Choose a Course --</option>' +
            courses.map(c => `<option value="${c.id}">${escapeHtml(c.courseCode)} - ${escapeHtml(c.courseName)} (${escapeHtml(c.semester)})</option>`).join("");

        if (currentVal && courses.some(c => c.id == currentVal)) {
            select.value = currentVal;
        }
    });
}

// ==========================================================================
// 2. Feedback Form Management
// ==========================================================================
function openFeedbackFormTabForCourse(courseId) {
    switchTab("feedback-forms-section");
    const select = document.getElementById("form-course-select");
    if (select) {
        select.value = courseId;
        onFormCourseSelected();
    }
}

function onFormCourseSelected() {
    const courseId = document.getElementById("form-course-select").value;
    if (!courseId) {
        document.getElementById("feedback-form-details").classList.add("hidden");
        document.getElementById("no-feedback-form-box").classList.add("hidden");
        return;
    }
    loadFeedbackForm(courseId);
}

async function loadFeedbackForm(courseId = null) {
    if (!courseId) {
        courseId = document.getElementById("form-course-select").value;
    }
    if (!courseId) return;

    currentSelectedCourseId = courseId;
    const detailsContainer = document.getElementById("feedback-form-details");
    const noFormBox = document.getElementById("no-feedback-form-box");

    try {
        // Fetch course info
        const courseRes = await fetch(`${API_BASE}/courses/${courseId}`);
        if (!courseRes.ok) throw new Error("Course not found.");
        const course = await courseRes.json();

        // Populate header
        document.getElementById("ff-course-code").textContent = course.courseCode;
        document.getElementById("ff-course-name").textContent = course.courseName;
        document.getElementById("ff-faculty-name").textContent = course.facultyName;
        document.getElementById("ff-semester").textContent = course.semester;

        // Fetch feedback form
        const formRes = await fetch(`${API_BASE}/feedback-forms/course/${courseId}`);

        if (formRes.status === 404) {
            // Form doesn't exist yet
            currentFeedbackForm = null;
            detailsContainer.classList.add("hidden");
            noFormBox.classList.remove("hidden");
            return;
        }

        if (!formRes.ok) {
            const err = await formRes.json();
            throw new Error(err.message || "Failed to load feedback form.");
        }

        const form = await formRes.json();
        currentFeedbackForm = form;

        noFormBox.classList.add("hidden");
        detailsContainer.classList.remove("hidden");

        // Status badge
        const statusBadge = document.getElementById("ff-status-badge");
        if (form.open) {
            statusBadge.className = "badge badge-open";
            statusBadge.textContent = "OPEN";
            document.getElementById("btn-open-feedback").style.display = "none";
            document.getElementById("btn-close-feedback").style.display = "inline-flex";
        } else {
            statusBadge.className = "badge badge-closed";
            statusBadge.textContent = "CLOSED";
            document.getElementById("btn-open-feedback").style.display = "inline-flex";
            document.getElementById("btn-close-feedback").style.display = "none";
        }

        // Deadline
        document.getElementById("ff-deadline-text").textContent = form.deadline || "None";

        // Questions list
        renderQuestionsList(form.questions || []);

    } catch (err) {
        showAlert(err.message, "error");
    }
}

function renderQuestionsList(questions) {
    const list = document.getElementById("questions-list");
    const empty = document.getElementById("questions-empty");

    if (!questions || questions.length === 0) {
        list.innerHTML = "";
        empty.classList.remove("hidden");
        return;
    }

    empty.classList.add("hidden");
    list.innerHTML = questions.map((q, idx) => `
        <li>
            <strong>Q${idx + 1}.</strong> ${escapeHtml(q.questionText)}
        </li>
    `).join("");
}

async function handleCreateFeedbackForm(e) {
    e.preventDefault();
    if (!currentSelectedCourseId) {
        showAlert("Please select a course first.", "error");
        return;
    }

    const deadline = document.getElementById("formDeadline").value;
    if (!deadline) {
        showAlert("Please specify a deadline date.", "error");
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/feedback-forms`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                courseId: parseInt(currentSelectedCourseId),
                deadline: deadline
            })
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || "Failed to create feedback form.");
        }

        showAlert("Feedback form created successfully.", "success");
        loadFeedbackForm(currentSelectedCourseId);
    } catch (err) {
        showAlert(err.message, "error");
    }
}

async function openFeedback() {
    if (!currentFeedbackForm) return;

    try {
        const response = await fetch(`${API_BASE}/feedback-forms/${currentFeedbackForm.id}/open`, {
            method: "PUT"
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || "Failed to open feedback form.");
        }

        showAlert("Feedback collection is now OPEN.", "success");
        loadFeedbackForm(currentSelectedCourseId);
    } catch (err) {
        showAlert(err.message, "error");
    }
}

async function closeFeedback() {
    if (!currentFeedbackForm) return;

    try {
        const response = await fetch(`${API_BASE}/feedback-forms/${currentFeedbackForm.id}/close`, {
            method: "PUT"
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || "Failed to close feedback form.");
        }

        showAlert("Feedback collection is now CLOSED.", "info");
        loadFeedbackForm(currentSelectedCourseId);
    } catch (err) {
        showAlert(err.message, "error");
    }
}

async function handleAddQuestion(e) {
    e.preventDefault();
    if (!currentFeedbackForm) {
        showAlert("No feedback form found. Please create one first.", "error");
        return;
    }

    const input = document.getElementById("newQuestionText");
    const text = input.value.trim();
    if (!text) return;

    const addBtn = document.getElementById("btn-add-question");
    addBtn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/feedback-forms/${currentFeedbackForm.id}/questions`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ questionText: text })
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || "Failed to add question.");
        }

        input.value = "";
        showAlert("Question added to feedback form.", "success");
        loadFeedbackForm(currentSelectedCourseId);
    } catch (err) {
        showAlert(err.message, "error");
    } finally {
        addBtn.disabled = false;
    }
}

// ==========================================================================
// 3. Student Feedback Submission
// ==========================================================================
function onFeedbackCourseSelected() {
    const courseId = document.getElementById("feedback-course-select").value;
    const container = document.getElementById("feedback-submission-container");

    if (!courseId) {
        container.classList.add("hidden");
        return;
    }

    loadStudentFeedbackForm(courseId);
}

async function loadStudentFeedbackForm(courseId) {
    const container = document.getElementById("feedback-submission-container");
    const warningBox = document.getElementById("sub-closed-warning");
    const questionsContainer = document.getElementById("feedback-questions-list");
    const submitBtn = document.getElementById("btn-submit-feedback");

    try {
        // Fetch course info
        const courseRes = await fetch(`${API_BASE}/courses/${courseId}`);
        if (!courseRes.ok) throw new Error("Course not found.");
        const course = await courseRes.json();

        // Update banner
        document.getElementById("sub-course-code").textContent = course.courseCode;
        document.getElementById("sub-course-name").textContent = course.courseName;
        document.getElementById("sub-faculty-name").textContent = course.facultyName;
        document.getElementById("sub-semester").textContent = course.semester;

        // Fetch feedback form
        const formRes = await fetch(`${API_BASE}/feedback-forms/course/${courseId}`);
        if (formRes.status === 404) {
            showAlert("No feedback form configured for this course yet.", "info");
            container.classList.add("hidden");
            return;
        }

        if (!formRes.ok) {
            const err = await formRes.json();
            throw new Error(err.message || "Error loading form.");
        }

        const form = await formRes.json();
        container.classList.remove("hidden");

        // Deadline & status check
        const isClosed = !form.open;
        const isExpired = form.deadline && new Date(form.deadline + "T23:59:59") < new Date();

        const statusBadge = document.getElementById("sub-status-badge");
        document.getElementById("sub-deadline").textContent = form.deadline || "No deadline";

        if (isClosed || isExpired) {
            statusBadge.className = "badge badge-closed";
            statusBadge.textContent = "CLOSED";
            warningBox.classList.remove("hidden");
            submitBtn.disabled = true;
        } else {
            statusBadge.className = "badge badge-open";
            statusBadge.textContent = "OPEN";
            warningBox.classList.add("hidden");
            submitBtn.disabled = false;
        }

        // Render questions
        if (!form.questions || form.questions.length === 0) {
            questionsContainer.innerHTML = '<div class="empty-text">No evaluation questions have been added to this form yet.</div>';
            submitBtn.disabled = true;
            return;
        }

        questionsContainer.innerHTML = form.questions.map((q, idx) => `
            <div class="question-block" data-question-id="${q.id}">
                <div class="question-prompt">
                    <span>${idx + 1}.</span> ${escapeHtml(q.questionText)} <span class="required">*</span>
                </div>
                <div class="rating-options">
                    ${[1, 2, 3, 4, 5].map(rating => `
                        <label class="rating-option" id="label-q${q.id}-r${rating}">
                            <input type="radio" name="rating_${q.id}" value="${rating}" required
                                   onchange="handleRatingSelection(${q.id}, ${rating})">
                            <span>${rating}</span>
                            <span class="rating-desc">${getRatingLabel(rating)}</span>
                        </label>
                    `).join("")}
                </div>
            </div>
        `).join("");

    } catch (err) {
        showAlert(err.message, "error");
        container.classList.add("hidden");
    }
}

function getRatingLabel(rating) {
    switch (rating) {
        case 1: return "Very Poor";
        case 2: return "Poor";
        case 3: return "Average";
        case 4: return "Good";
        case 5: return "Excellent";
        default: return "";
    }
}

function handleRatingSelection(questionId, rating) {
    // Style active rating button
    const siblings = document.querySelectorAll(`[name="rating_${questionId}"]`);
    siblings.forEach(radio => {
        const label = document.getElementById(`label-q${questionId}-r${radio.value}`);
        if (label) {
            if (radio.checked) {
                label.classList.add("selected");
            } else {
                label.classList.remove("selected");
            }
        }
    });
}

async function handleSubmitFeedback(e) {
    e.preventDefault();

    const courseId = document.getElementById("feedback-course-select").value;
    const studentId = document.getElementById("studentId").value.trim();

    if (!courseId) {
        showAlert("Please select a course.", "error");
        return;
    }
    if (!studentId) {
        showAlert("Please enter your Student ID.", "error");
        return;
    }

    const questionBlocks = document.querySelectorAll(".question-block");
    if (questionBlocks.length === 0) {
        showAlert("No questions to evaluate.", "error");
        return;
    }

    const ratings = [];
    for (const block of questionBlocks) {
        const qId = block.getAttribute("data-question-id");
        const selectedRadio = block.querySelector(`input[name="rating_${qId}"]:checked`);
        if (!selectedRadio) {
            showAlert("Please answer all rating questions before submitting.", "error");
            block.scrollIntoView({ behavior: "smooth", block: "center" });
            return;
        }
        ratings.push({
            questionId: parseInt(qId),
            rating: parseInt(selectedRadio.value)
        });
    }

    const submitBtn = document.getElementById("btn-submit-feedback");
    submitBtn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/feedback`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                courseId: parseInt(courseId),
                studentId: studentId,
                ratings: ratings
            })
        });

        const data = await response.json();

        if (response.status === 409) {
            showAlert("Student has already submitted feedback for this course and semester.", "error");
            return;
        }

        if (!response.ok) {
            throw new Error(data.message || "Failed to submit feedback.");
        }

        showAlert(data.message || "Feedback submitted successfully.", "success");

        // Clear form
        document.getElementById("studentId").value = "";
        document.querySelectorAll('input[type="radio"]').forEach(r => {
            r.checked = false;
        });
        document.querySelectorAll(".rating-option").forEach(lbl => {
            lbl.classList.remove("selected");
        });

    } catch (err) {
        showAlert(err.message, "error");
    } finally {
        submitBtn.disabled = false;
    }
}

// ==========================================================================
// 4. Course Feedback Summary
// ==========================================================================
function openSummaryTabForCourse(courseId) {
    switchTab("course-summary-section");
    const select = document.getElementById("summary-course-select");
    if (select) {
        select.value = courseId;
        loadCourseSummary();
    }
}

function onSummaryCourseSelected() {
    loadCourseSummary();
}

async function loadCourseSummary() {
    const courseId = document.getElementById("summary-course-select").value;
    const container = document.getElementById("course-summary-container");

    if (!courseId) {
        container.classList.add("hidden");
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/feedback/course/${courseId}/summary`);
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || "Failed to load summary.");
        }

        const summary = await response.json();
        container.classList.remove("hidden");

        document.getElementById("sum-course-name").textContent = summary.courseName;
        document.getElementById("sum-faculty-name").textContent = `Faculty: ${summary.facultyName}`;
        document.getElementById("sum-total-responses").textContent = summary.totalResponses;
        document.getElementById("sum-overall-average").textContent = summary.overallAverage.toFixed(2);

        // Verbal description
        const verbal = document.getElementById("sum-rating-verbal");
        if (summary.overallAverage >= 4.0) {
            verbal.textContent = "Excellent Performance";
            verbal.style.color = "var(--success)";
        } else if (summary.overallAverage >= 3.0) {
            verbal.textContent = "Good / Average Performance";
            verbal.style.color = "var(--warning)";
        } else if (summary.overallAverage > 0) {
            verbal.textContent = "Needs Attention";
            verbal.style.color = "var(--danger)";
        } else {
            verbal.textContent = "No Ratings Recorded";
            verbal.style.color = "var(--grey-medium)";
        }

        // Questions table
        const tbody = document.getElementById("summary-questions-tbody");
        const noQuestions = document.getElementById("summary-no-questions");

        if (!summary.questions || summary.questions.length === 0) {
            tbody.innerHTML = "";
            noQuestions.classList.remove("hidden");
            return;
        }

        noQuestions.classList.add("hidden");
        tbody.innerHTML = summary.questions.map((q, idx) => {
            const scoreClass = q.averageRating >= 4.0 ? "score-high" : (q.averageRating >= 3.0 ? "score-medium" : "score-low");
            const percentage = ((q.averageRating / 5.0) * 100).toFixed(0);

            return `
                <tr>
                    <td><strong>${idx + 1}</strong></td>
                    <td>${escapeHtml(q.question)}</td>
                    <td>
                        <span class="score-badge ${scoreClass}">${q.averageRating.toFixed(2)} / 5</span>
                    </td>
                    <td>
                        <div class="bar-container" title="${percentage}% satisfaction">
                            <div class="bar-fill" style="width: ${percentage}%;"></div>
                        </div>
                    </td>
                </tr>
            `;
        }).join("");

    } catch (err) {
        showAlert(err.message, "error");
        container.classList.add("hidden");
    }
}

// ==========================================================================
// 5. Faculty Summary
// ==========================================================================
function handleFacultySearch(e) {
    e.preventDefault();
    const facultyInput = document.getElementById("faculty-search-input").value.trim();
    if (!facultyInput) return;
    loadFacultySummary(facultyInput);
}

async function loadFacultySummary(facultyName) {
    const resultsContainer = document.getElementById("faculty-summary-results");
    const tbody = document.getElementById("faculty-courses-tbody");
    const emptyNotice = document.getElementById("faculty-empty-results");
    const displayFaculty = document.getElementById("faculty-name-display");

    try {
        const response = await fetch(`${API_BASE}/feedback/faculty/${encodeURIComponent(facultyName)}/summary`);
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || "Failed to load faculty summary.");
        }

        const list = await response.json();
        resultsContainer.classList.remove("hidden");
        displayFaculty.textContent = facultyName;

        if (!list || list.length === 0) {
            tbody.innerHTML = "";
            emptyNotice.classList.remove("hidden");
            return;
        }

        emptyNotice.classList.add("hidden");
        tbody.innerHTML = list.map(item => {
            const scoreClass = item.averageRating >= 4.0 ? "score-high" : (item.averageRating >= 3.0 ? "score-medium" : "score-low");
            return `
                <tr>
                    <td><span class="badge badge-code">${escapeHtml(item.courseCode)}</span></td>
                    <td><strong>${escapeHtml(item.courseName)}</strong></td>
                    <td>${escapeHtml(item.semester)}</td>
                    <td>${item.totalResponses}</td>
                    <td>
                        <span class="score-badge ${scoreClass}">${item.averageRating.toFixed(2)} / 5.0</span>
                    </td>
                </tr>
            `;
        }).join("");

    } catch (err) {
        showAlert(err.message, "error");
        resultsContainer.classList.add("hidden");
    }
}

// ==========================================================================
// Utility: HTML Escaping
// ==========================================================================
function escapeHtml(str) {
    if (!str) return "";
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
