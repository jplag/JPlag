import module java.base;
import module java.sql;

public class Java25 {

    // Compact main method
    void main() {
        // Module imports:
        List<String> list = List.of("a", "b", "c"); // java.util.List from java.base is imported
        java.sql.Date date = new java.sql.Date(0);  // java.sql.Date from java.sql is imported

        // Unnamed variables:
        int count;
        for (String _ : list) {// loop variable is unused
            count++;
        }

    }

    class Person {
        Person(int age) {
            /* ... */ }
    }

    class Employee extends Person {
        Employee(int age) {
            if (age < 0)
                age = 0;   // validate before calling super
            super(age);            // now allowed after other statements
            IO.println("Employee age: " + age);
        }
    }
}