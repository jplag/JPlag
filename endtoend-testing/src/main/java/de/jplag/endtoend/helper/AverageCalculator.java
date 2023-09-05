package de.jplag.endtoend.helper;

/**
 * Helper to calculate the average of multiple numbers.
 */
public class AverageCalculator {
    private double sum;
    private int count;

    /**
     * Creates a new empty calculator
     */
    public AverageCalculator() {
        this.sum = 0;
        this.count = 0;
    }

    /**
     * Adds a new value to the calculation
     * @param value The value
     */
    public void add(double value) {
        this.sum += value;
        this.count++;
    }

    /**
     * Calculates and returns the average of all added values.
     * @return The result
     */
    public double calculate() {
        return this.sum / count;
    }
}
