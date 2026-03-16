public class StudentGradeCalculator {
    private double[] grades;
    private String studentName;

    public StudentGradeCalculator(String name, double[] gradeList) {
        this.studentName = name;
        this.grades = gradeList;
    }

    public double calculateAverage() {
        double sum = 0;
        for (double grade : grades) {
            sum += grade;
        }
        return sum / grades.length;
    }

    public String getGrade() {
        double avg = calculateAverage();
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }

    //DeadCodeStart
    public void unusedMethodForLogging() {
        System.out.println("This method is never called");
        int unusedVariable = 42;
        String deadString = "This code is dead";
        for (int i = 0; i < 100; i++) {
            // Pointless loop that does nothing
        }
    }
    //DeadCodeEnd

    public void displayResult() {
        System.out.println("Student: " + studentName);
        System.out.println("Average: " + calculateAverage());
        System.out.println("Grade: " + getGrade());
    }
}

public class Main {
    public static void main(String[] args) {
        double[] grades = {85, 90, 78, 92, 88};
        StudentGradeCalculator calculator = new StudentGradeCalculator("John Doe", grades);
        calculator.displayResult();
    }
}
