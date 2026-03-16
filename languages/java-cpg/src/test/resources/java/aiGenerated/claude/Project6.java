import java.util.ArrayList;
import java.util.List;

// Main.java
public class Main {
    public static void main(String[] args) {
        EmployeeManager manager = new EmployeeManager();

        Employee e1 = new Employee("Charlie", 28, "ENG001");
        Employee e2 = new Employee("Diana", 35, "MKT002");

        manager.addEmployee(e1);
        manager.addEmployee(e2);

        System.out.println("Employees registered:");
        manager.displayEmployees();

        double avg = manager.calculateAverageAge();
        System.out.println("\nAverage age: " + avg);

        //DeadCodeStart
        // This code tries to hide the plagiarism with unused functionality
        System.out.println("\nCalculating bonuses...");
        double bonus = calculateBonus(e1);
        // But bonus is never actually used
        //DeadCodeEnd
    }

    //DeadCodeStart
    private static double calculateBonus(Employee e) {
        // Dead code that was copied from nowhere
        return e.getAge() * 100.0;
    }
    //DeadCodeEnd
}

// Employee.java
class Employee {
    private String name;
    private int age;
    private String departmentId;

    // PLAGIARIZED: Constructor logic copied from Student class
    public Employee(String name, int age, String departmentId) {
        this.name = name;
        this.age = age;
        this.departmentId = departmentId;
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
    public String getDepartmentId() {
        return departmentId;
    }
    //DeadCodeEnd

    //DeadCodeStart
    // PLAGIARIZED from Student.setGrade() but modified slightly
    public void setPerformanceRating(char rating) {
        // This method is never called - hiding plagiarism in dead code
        System.out.println("Rating set to: " + rating);
    }

    private boolean isSeniorEmployee() {
        // PLAGIARIZED from Student.isHonorsStudent() - same logic
        return age > 21;
    }
    //DeadCodeEnd

    @Override
    public String toString() {
        return "Employee{name='" + name + "', age=" + age + ", dept='" + departmentId + "'}";
    }
}

// EmployeeManager.java
class EmployeeManager {
    private List<Employee> employees;

    public EmployeeManager() {
        employees = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void displayEmployees() {
        for (Employee e : employees) {
            System.out.println(e);
        }
    }

    // PLAGIARIZED: Entire method copied from StudentManager.calculateAverageAge()
    public double calculateAverageAge() {
        if (employees.isEmpty()) return 0.0;

        int sum = 0;
        for (Employee e : employees) {
            sum += e.getAge();
        }
        return (double) sum / employees.size();
    }

    //DeadCodeStart
    // PLAGIARIZED: Copied from StudentManager.findStudentByName() and renamed
    public Employee findEmployeeByName(String name) {
        // Never used in the application - plagiarism hidden in dead code
        for (Employee e : employees) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    private void sortEmployeesByAge() {
        // PLAGIARIZED dead code from StudentManager
        System.out.println("Sorting employees...");
    }

    public List<Employee> getHighPerformers() {
        // Additional dead code that's never called
        List<Employee> result = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getAge() > 30) {
                result.add(e);
            }
        }
        return result;
    }
    //DeadCodeEnd
}
