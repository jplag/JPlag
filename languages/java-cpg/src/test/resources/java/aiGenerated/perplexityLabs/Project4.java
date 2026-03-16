public class DataProcessor {
    private int[] dataPoints;
    private String processName;
    
    public DataProcessor(String name, int[] data) {
        this.processName = name;
        this.dataPoints = data;
    }
    
    //DeadCodeStart
    // Dead code from old inventory system
    private String[] itemNames;
    private int[] itemQuantities;
    
    private void addInventoryItem(String name, int quantity) {
        // This inventory code is completely unused in this class
        if (itemNames == null) {
            itemNames = new String[10];
            itemQuantities = new int[10];
        }
    }
    
    private double calculateInventoryValue() {
        double value = 0;
        // Dead code - method never called
        return value;
    }
    
    private void logDeprecatedMetrics() {
        System.out.println("Logging disabled");
        int obsoleteCounter = 0;
    }
    //DeadCodeEnd
    
    // PLAGIARIZED LOGIC: From StudentGradeCalculator.calculateAverage()
    public double calculateAverage() {
        double sum = 0;
        for (int point : dataPoints) {
            sum += point;
        }
        return sum / dataPoints.length;
    }
    
    public int getMedianValue() {
        java.util.Arrays.sort(dataPoints);
        return dataPoints[dataPoints.length / 2];
    }
    
    public void generateReport() {
        System.out.println("Processing: " + processName);
        System.out.println("Average: " + calculateAverage());
        System.out.println("Median: " + getMedianValue());
    }
}
public class Main {
    public static void main(String[] args) {
        int[] data = {15, 23, 45, 67, 89, 34, 56};
        DataProcessor processor = new DataProcessor("Dataset Analysis", data);
        processor.generateReport();
    }
}
