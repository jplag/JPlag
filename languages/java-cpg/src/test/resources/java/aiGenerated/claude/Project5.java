import java.util.ArrayList;
import java.util.List;

// Main.java
public class Main {
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();

        Student s1 = new Student("Alice", 20, "CS101");
        Student s2 = new Student("Bob", 22, "CS102");

        manager.addStudent(s1);
        manager.addStudent(s2);

        System.out.println("Students enrolled:");
        manager.displayStudents();

        double avg = manager.calculateAverageAge();
        System.out.println("\nAverage age: " + avg);
    }
}

// Student.java
class Student {
    private String name;
    private int age;
    private String courseId;

    public Student(String name, int age, String courseId) {
        this.name = name;
        this.age = age;
        this.courseId = courseId;
    }

    //DeadCodeStart
    public String getName() {
        return name;
    }
    //DeadCodeEnd

    public int getAge() {
        return age;
    }

    //DeadCodeStart
    public String getCourseId() {
        return courseId;
    }

    public void setGrade(char grade) {
        // This method is never called
        System.out.println("Grade set to: " + grade);
    }

    private boolean isHonorsStudent() {
        // Unused helper method
        return age > 21;
    }
    //DeadCodeEnd

    @Override
    public String toString() {
        return "Student{name='" + name + "', age=" + age + ", course='" + courseId + "'}";
    }
}

// StudentManager.java

class StudentManager {
    private List<Student> students;

    public StudentManager() {
        students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void displayStudents() {
        for (Student s : students) {
            System.out.println(s);
        }
    }

    public double calculateAverageAge() {
        if (students.isEmpty()) return 0.0;

        int sum = 0;
        for (Student s : students) {
            sum += s.getAge();
        }
        return (double) sum / students.size();
    }

    //DeadCodeStart
    public Student findStudentByName(String name) {
        // Never used in the application
        for (Student s : students) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    private void sortStudentsByAge() {
        // Incomplete dead code
        System.out.println("Sorting students...");
    }
    //DeadCodeEnd
}
