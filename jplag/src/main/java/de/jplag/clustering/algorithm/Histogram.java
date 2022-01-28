package de.jplag.clustering.algorithm;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.stream.DoubleStream;

public class Histogram {
    
    private double[] bins;
    private double[] borders;

    public Histogram(double[] bins, double[] borders) {
        this.bins = bins;
        this.borders = borders;
    }

    public double predict(double x) {
        int index = Arrays.binarySearch(borders, x);
        if (index == 0 || index == borders.length) return 0;
        return bins[index - 1];
    }

    public static Histogram fit(double[] data) {
        return fit(data, 30);
    }

    public static Histogram fit(double[] data, int bins) {
        DoubleSummaryStatistics stats = DoubleStream.of(data).summaryStatistics();
        double min = stats.getMin();
        double max = stats.getMax();
        double[] binArr = new double[bins]; 
        for (double v : data) {
            int bin = Math.min((int) (bins * (v - min) / (max - min)), bins - 1);
            binArr[bin] += 1;
        }
        for (int i = 0; i < bins; i++) {
            binArr[i] /= data.length;
        }
        double[] borders = new double[bins + 1];
        for (int i = 0; i < bins + 1; i++) {
            borders[i] = min + i * (max - min) / bins;
        }
        return new Histogram(binArr, borders);
    }

}
