import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CourseRegistrationServer {

    private static final int PORT = 8080;

    private static final List<Course> courses =
            new ArrayList<>();

    private static final List<Student> students =
            new ArrayList<>();


    public static void main(String[] args) throws Exception {

        loadCourses();
        loadStudents();

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT), 0);

        server.createContext("/", CourseRegistrationServer::handleRoot);

        server.createContext(
                "/student",
                CourseRegistrationServer::handleStudent
        );

        server.createContext(
                "/courses",
                CourseRegistrationServer::handleCourses
        );

        server.createContext(
                "/register",
                CourseRegistrationServer::handleRegister
        );

        server.createContext(
                "/registered",
                CourseRegistrationServer::handleRegistered
        );

        server.createContext(
                "/drop",
                CourseRegistrationServer::handleDrop
        );

        server.start();

        System.out.println("======================================");
        System.out.println("   STUDENT COURSE REGISTRATION");
        System.out.println("======================================");
        System.out.println("Java Backend Started Successfully!");
        System.out.println("Running at: http://localhost:8080");
        System.out.println("Courses: " + courses.size());
        System.out.println("Students: " + students.size());
        System.out.println("Waiting for frontend requests...");
    }


    // ---------------- COURSES ----------------

    private static void loadCourses() {

        courses.add(
                new Course(
                        "CS101",
                        "Java Programming",
                        "Learn Java programming fundamentals.",
                        3,
                        "Monday & Wednesday - 10:00 AM"
                )
        );

        courses.add(
                new Course(
                        "CS102",
                        "Database Management",
                        "Learn SQL, DBMS and database concepts.",
                        4,
                        "Tuesday & Thursday - 11:00 AM"
                )
        );

        courses.add(
                new Course(
                        "CS103",
                        "Web Development",
                        "Learn HTML, CSS and JavaScript.",
                        3,
                        "Monday & Friday - 2:00 PM"
                )
        );

        courses.add(
                new Course(
                        "CS104",
                        "Data Structures",
                        "Learn arrays, linked lists, stacks and queues.",
                        4,
                        "Wednesday & Friday - 11:00 AM"
                )
        );

        courses.add(
                new Course(
                        "CS105",
                        "Computer Networks",
                        "Learn networking fundamentals and protocols.",
                        3,
                        "Tuesday & Thursday - 3:00 PM"
                )
        );
    }


    // ---------------- STUDENTS ----------------

    private static void loadStudents() {

        students.add(
                new Student(101, "Asish")
        );

        students.add(
                new Student(102, "Rahul")
        );

        students.add(
                new Student(103, "Priya")
        );

        students.add(
                new Student(104, "Aman")
        );
    }


    // ---------------- ROOT ----------------

    private static void handleRoot(
            HttpExchange exchange) throws IOException {

        sendResponse(
                exchange,
                "{\"status\":\"Course Registration Backend Running\"}"
        );
    }


    // ---------------- STUDENT ----------------

    private static void handleStudent(
            HttpExchange exchange) throws IOException {

        addCors(exchange);

        String query = exchange.getRequestURI().getQuery();

        int id = Integer.parseInt(
                getParameter(query, "id")
        );

        Student student = findStudent(id);

        if (student == null) {

            sendResponse(
                    exchange,
                    "{\"success\":false,\"message\":\"Student not found.\"}"
            );

            return;
        }

        String json =
                "{"
                + "\"success\":true,"
                + "\"name\":\"" + student.getName() + "\""
                + "}";

        sendResponse(exchange, json);
    }


    // ---------------- COURSES ----------------

    private static void handleCourses(
            HttpExchange exchange) throws IOException {

        addCors(exchange);

        String query =
                exchange.getRequestURI().getQuery();

        int studentId = Integer.parseInt(
                getParameter(query, "studentId")
        );

        Student student = findStudent(studentId);

        StringBuilder json =
                new StringBuilder("[");

        for (int i = 0; i < courses.size(); i++) {

            Course course = courses.get(i);

            int registeredCount =
                    getRegisteredCount(course.getCode());

            int availableSlots =
                    course.getCapacity() - registeredCount;

            boolean registered =
                    student != null &&
                    student.isRegistered(course.getCode());

            json.append("{")
                    .append("\"code\":\"")
                    .append(course.getCode())
                    .append("\",")

                    .append("\"title\":\"")
                    .append(course.getTitle())
                    .append("\",")

                    .append("\"description\":\"")
                    .append(course.getDescription())
                    .append("\",")

                    .append("\"capacity\":")
                    .append(course.getCapacity())
                    .append(",")

                    .append("\"schedule\":\"")
                    .append(course.getSchedule())
                    .append("\",")

                    .append("\"availableSlots\":")
                    .append(availableSlots)
                    .append(",")

                    .append("\"registered\":")
                    .append(registered)

                    .append("}");

            if (i < courses.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        sendResponse(exchange, json.toString());
    }


    // ---------------- REGISTER ----------------

    private static void handleRegister(
            HttpExchange exchange) throws IOException {

        addCors(exchange);

        String query =
                exchange.getRequestURI().getQuery();

        int studentId = Integer.parseInt(
                getParameter(query, "studentId")
        );

        String courseCode =
                getParameter(query, "courseCode");

        Student student =
                findStudent(studentId);

        Course course =
                findCourse(courseCode);


        if (student == null) {

            sendResponse(
                    exchange,
                    error("Student not found.")
            );

            return;
        }

        if (course == null) {

            sendResponse(
                    exchange,
                    error("Course not found.")
            );

            return;
        }

        if (student.isRegistered(courseCode)) {

            sendResponse(
                    exchange,
                    error("You are already registered for this course.")
            );

            return;
        }

        int availableSlots =
                course.getCapacity()
                - getRegisteredCount(courseCode);

        if (availableSlots <= 0) {

            sendResponse(
                    exchange,
                    error("Course is full.")
            );

            return;
        }

        student.registerCourse(courseCode);

        sendResponse(
                exchange,
                success(
                        "Course registered successfully."
                )
        );
    }


    // ---------------- REGISTERED ----------------

    private static void handleRegistered(
            HttpExchange exchange) throws IOException {

        addCors(exchange);

        String query =
                exchange.getRequestURI().getQuery();

        int studentId = Integer.parseInt(
                getParameter(query, "studentId")
        );

        Student student =
                findStudent(studentId);

        StringBuilder json =
                new StringBuilder("[");

        if (student != null) {

            List<String> registered =
                    student.getRegisteredCourses();

            for (int i = 0; i < registered.size(); i++) {

                Course course =
                        findCourse(registered.get(i));

                if (course == null) {
                    continue;
                }

                json.append("{")
                        .append("\"code\":\"")
                        .append(course.getCode())
                        .append("\",")

                        .append("\"title\":\"")
                        .append(course.getTitle())
                        .append("\",")

                        .append("\"schedule\":\"")
                        .append(course.getSchedule())
                        .append("\"")
                        .append("}");

                if (i < registered.size() - 1) {
                    json.append(",");
                }
            }
        }

        json.append("]");

        sendResponse(exchange, json.toString());
    }


    // ---------------- DROP ----------------

    private static void handleDrop(
            HttpExchange exchange) throws IOException {

        addCors(exchange);

        String query =
                exchange.getRequestURI().getQuery();

        int studentId = Integer.parseInt(
                getParameter(query, "studentId")
        );

        String courseCode =
                getParameter(query, "courseCode");

        Student student =
                findStudent(studentId);

        if (student == null) {

            sendResponse(
                    exchange,
                    error("Student not found.")
            );

            return;
        }

        if (!student.isRegistered(courseCode)) {

            sendResponse(
                    exchange,
                    error("You are not registered for this course.")
            );

            return;
        }

        student.dropCourse(courseCode);

        sendResponse(
                exchange,
                success(
                        "Course dropped successfully."
                )
        );
    }


    // ---------------- HELPERS ----------------

    private static Student findStudent(int id) {

        for (Student student : students) {

            if (student.getStudentId() == id) {
                return student;
            }
        }

        return null;
    }


    private static Course findCourse(String code) {

        for (Course course : courses) {

            if (course.getCode().equalsIgnoreCase(code)) {
                return course;
            }
        }

        return null;
    }


    private static int getRegisteredCount(
            String courseCode) {

        int count = 0;

        for (Student student : students) {

            if (student.isRegistered(courseCode)) {
                count++;
            }
        }

        return count;
    }


    private static String getParameter(
            String query,
            String name) {

        if (query == null) {
            return "";
        }

        for (String parameter : query.split("&")) {

            String[] pair =
                    parameter.split("=", 2);

            if (pair.length == 2 &&
                    pair[0].equals(name)) {

                return URLDecoder.decode(
                        pair[1],
                        StandardCharsets.UTF_8
                );
            }
        }

        return "";
    }


    private static String success(String message) {

        return "{"
                + "\"success\":true,"
                + "\"message\":\"" + message + "\""
                + "}";
    }


    private static String error(String message) {

        return "{"
                + "\"success\":false,"
                + "\"message\":\"" + message + "\""
                + "}";
    }


    private static void addCors(
            HttpExchange exchange) {

        exchange.getResponseHeaders()
                .add(
                        "Access-Control-Allow-Origin",
                        "*"
                );

        exchange.getResponseHeaders()
                .add(
                        "Access-Control-Allow-Methods",
                        "GET, OPTIONS"
                );

        exchange.getResponseHeaders()
                .add(
                        "Access-Control-Allow-Headers",
                        "Content-Type"
                );

        exchange.getResponseHeaders()
                .add(
                        "Content-Type",
                        "application/json"
                );
    }


    private static void sendResponse(
            HttpExchange exchange,
            String response) throws IOException {

        addCors(exchange);

        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.sendResponseHeaders(
                200,
                bytes.length
        );

        try (OutputStream os =
                     exchange.getResponseBody()) {

            os.write(bytes);
        }
    }
}