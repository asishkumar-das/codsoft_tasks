const API_URL = "http://localhost:8080";

let currentStudentId = null;


// Load Student
async function loadStudent() {

    const id = document.getElementById("studentId").value;
    const message = document.getElementById("studentMessage");

    if (!id) {
        message.textContent = "Please enter Student ID.";
        message.className = "error";
        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/student?id=${id}`
        );

        const data = await response.json();

        if (!data.success) {
            message.textContent = data.message;
            message.className = "error";

            document.getElementById("courseContainer").innerHTML = "";
            document.getElementById("registeredContainer").innerHTML = "";

            return;
        }

        currentStudentId = id;

        message.textContent = `Welcome, ${data.name}!`;
        message.className = "success";

        loadCourses();
        loadRegisteredCourses();

    } catch (error) {

        message.textContent = "Unable to connect to Java backend.";
        message.className = "error";

    }
}


// Load Available Courses
async function loadCourses() {

    try {

        const response = await fetch(
            `${API_URL}/courses?studentId=${currentStudentId}`
        );

        const courses = await response.json();

        const container =
            document.getElementById("courseContainer");

        container.innerHTML = "";

        courses.forEach(course => {

            const card = document.createElement("div");

            card.className = "course-card";

            card.innerHTML = `
                <h3>${course.title}</h3>

                <p class="course-code">
                    Course Code: ${course.code}
                </p>

                <p>
                    ${course.description}
                </p>

                <p>
                    <strong>Schedule:</strong>
                    ${course.schedule}
                </p>

                <p class="slots">
                    Available Slots:
                    ${course.availableSlots}/${course.capacity}
                </p>

                <button
                    class="register-btn"
                    onclick="registerCourse('${course.code}')"
                    ${course.availableSlots === 0 ||
                      course.registered ? "disabled" : ""}
                >
                    ${
                        course.registered
                        ? "Already Registered"
                        : course.availableSlots === 0
                        ? "Course Full"
                        : "Register"
                    }
                </button>
            `;

            container.appendChild(card);

        });

    } catch (error) {

        document.getElementById("courseContainer").innerHTML =
            "<p class='error'>Unable to load courses.</p>";

    }
}


// Register Course
async function registerCourse(code) {

    try {

        const response = await fetch(
            `${API_URL}/register?studentId=${currentStudentId}&courseCode=${encodeURIComponent(code)}`
        );

        const data = await response.json();

        alert(data.message);

        if (data.success) {
            loadCourses();
            loadRegisteredCourses();
        }

    } catch (error) {

        alert("Unable to connect to backend.");

    }
}


// Load Registered Courses
async function loadRegisteredCourses() {

    try {

        const response = await fetch(
            `${API_URL}/registered?studentId=${currentStudentId}`
        );

        const courses = await response.json();

        const container =
            document.getElementById("registeredContainer");

        container.innerHTML = "";

        if (courses.length === 0) {

            container.innerHTML =
                "<p>No courses registered.</p>";

            return;
        }

        courses.forEach(course => {

            const card = document.createElement("div");

            card.className = "registered-card";

            card.innerHTML = `
                <div>
                    <strong>${course.code} - ${course.title}</strong>
                    <p>${course.schedule}</p>
                </div>

                <button
                    class="drop-btn"
                    onclick="dropCourse('${course.code}')">
                    Drop Course
                </button>
            `;

            container.appendChild(card);

        });

    } catch (error) {

        document.getElementById("registeredContainer").innerHTML =
            "<p class='error'>Unable to load registered courses.</p>";

    }
}


// Drop Course
async function dropCourse(code) {

    const confirmDrop =
        confirm("Are you sure you want to drop this course?");

    if (!confirmDrop) {
        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/drop?studentId=${currentStudentId}&courseCode=${encodeURIComponent(code)}`
        );

        const data = await response.json();

        alert(data.message);

        if (data.success) {
            loadCourses();
            loadRegisteredCourses();
        }

    } catch (error) {

        alert("Unable to connect to backend.");

    }
}