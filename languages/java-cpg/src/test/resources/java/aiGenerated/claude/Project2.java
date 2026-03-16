package com.example.students;

public class Main {
    public static void main(String[] args) {
        StudentGrades grades = new StudentGrades();
        
        // This looks different but uses plagiarized calculator logic
        System.out.println("Total score: " + grades.calculateTotal(85, 92));
        System.out.println("Score difference: " + grades.calculateDifference(92, 85));
        
        grades.displayStudent("John Doe", 85);
    }
}

class StudentGrades {
    // Plagiarized from Calculator.add()
    public int calculateTotal(int score1, int score2) {
        return score1 + score2;
    }
    
    // Plagiarized from Calculator.subtract()
    public int calculateDifference(int score1, int score2) {
        return score1 - score2;
    }
    
    //DeadCodeStart
    // Hidden plagiarism - multiply method copied but never used
    public int calculateProduct(int score1, int score2) {
        return score1 * score2;
    }
    
    // More plagiarized code hidden in dead code section
    public double calculateRatio(double score1, double score2) {
        if (score2 == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return score1 / score2;
    }
    //DeadCodeEnd
    
    public void displayStudent(String name, int grade) {
        System.out.println("Student: " + name + ", Grade: " + grade);
    }
}
