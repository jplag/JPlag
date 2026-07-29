import java.util.List;
import java.sql.Date;

public class Java25 {

    // No compact main method
    public static void main(String[] args) {
        // No module imports:
        List<String> list = List.of("a", "b", "c"); // java.util.List from java.base is imported
        java.sql.Date date = new java.sql.Date(0);  // java.sql.Date from java.sql is imported

        // No unnamed variables:
        int count;
        for (String text : list) {// loop variable is unused
            count++;
        }

    }

    class Person {
        Person(int age) {
        }
    }

    class Employee extends Person {
        Employee(int age) {
            if (age < 0) {
                age = 0;   // validate before calling super
            }
            super(age);            // now allowed after other statements
            System.out.println("Employee age: " + age);
        }
    }
}