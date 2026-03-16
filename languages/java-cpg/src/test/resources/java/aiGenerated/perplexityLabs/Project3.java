public class PlagiarizedGradeAnalyzer {
    private double[] scores;
    private String studentName;
    
    public PlagiarizedGradeAnalyzer(String name, double[] scoreList) {
        this.studentName = name;
        this.scores = scoreList;
    }
    
    //DeadCodeStart
    // Obfuscation: Dead code to hide the plagiarized method below
    private double calculateMedian() {
        // This method is not used anywhere
        java.util.Arrays.sort(scores);
        if (scores.length % 2 == 0) {
            return (scores[scores.length/2 - 1] + scores[scores.length/2]) / 2;
        }
        return scores[scores.length/2];
    }
    
    private void printDebugInfo() {
        System.out.println("Debug: internal state");
        int debugCounter = 0;
        while (debugCounter < 50) {
            debugCounter++;
        }
        boolean isDebugMode = false;
    }
    //DeadCodeEnd
    
    // PLAGIARIZED: Identical logic to StudentGradeCalculator.calculateAverage()
    public double calculateAverage() {
        double sum = 0;
        for (double grade : scores) {
            sum += grade;
        }
        return sum / scores.length;
    }
    
    // PLAGIARIZED: Identical logic to StudentGradeCalculator.getGrade()
    public String getGrade() {
        double avg = calculateAverage();
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }
    
    public void showAnalysis() {
        System.out.println("Student: " + studentName);
        System.out.println("Average: " + calculateAverage());
        System.out.println("Grade: " + getGrade());
    }
}
public class Main {
    public static void main(String[] args) {
        double[] scores = {85, 90, 78, 92, 88};
        PlagiarizedGradeAnalyzer analyzer = new PlagiarizedGradeAnalyzer("Jane Smith", scores);
        analyzer.showAnalysis();
    }
}
