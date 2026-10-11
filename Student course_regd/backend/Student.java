import java.util.ArrayList;
import java.util.List;

public class Student {

    private int studentId;
    private String name;

    private List<String> registeredCourses =
            new ArrayList<>();

    public Student(int studentId, String name) {

        this.studentId = studentId;
        this.name = name;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public List<String> getRegisteredCourses() {
        return registeredCourses;
    }

    public boolean isRegistered(String courseCode) {

        return registeredCourses.contains(courseCode);
    }

    public void registerCourse(String courseCode) {

        registeredCourses.add(courseCode);
    }

    public void dropCourse(String courseCode) {

        registeredCourses.remove(courseCode);
    }
}