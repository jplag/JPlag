package de.jplag.clustering.algorithm;

import java.text.MessageFormat;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.DoubleToIntFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;

/**
 * Implementation of a gaussian process with a matern kernel. This class can be used to fit any real-valued function
 * with noisy evaluations. Predictions come in the form of expectations and standard deviations.
 */
public class GaussianProcess {

    private List<RealVector> X;
    private RealVector weight;
    private double mean;
    private double standardDeviation;
    private CholeskyDecomposition cholesky;
    private RealVector lengthScale;

    private GaussianProcess(List<RealVector> X, RealVector weight, double mean, double standardDeviation, CholeskyDecomposition cholesky,
            RealVector lengthScale) {
        this.X = X;
        this.weight = weight;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.cholesky = cholesky;
        this.lengthScale = lengthScale;
    }

    /**
     * @param x coordinate to predict at
     * @return array containing the predicted [mean, standard deviation] at x.
     */
    public double[] predict(RealVector x) {
        RealVector kernelizedX = maternKernel(X, x, lengthScale);
        RealVector kernelMatrixTimesX = cholesky.getSolver().solve(kernelizedX);

        double predictedMean = weight.dotProduct(kernelizedX);
        double predictedStandardDeviation = Math.sqrt(maternKernel(x, x, lengthScale) - kernelMatrixTimesX.dotProduct(kernelizedX));

        double[] out = new double[2];
        out[0] = predictedMean * this.standardDeviation + this.mean;
        out[1] = predictedStandardDeviation * this.standardDeviation;

        if (Double.isNaN(out[1])) {
            out[1] = 0;
        }

        return out;
    }

    private static final double SQRT_5 = Math.sqrt(5);

    /**
     * Fit Gaussian Process using a matern kernel.
     * @param X
     * @param Y expected to have zero mean, unit variance if normalize is false
     * @param noise variance of noise in Y
     * @param normalize if Y should be normalized
     * @param lengthScale X values are divided by these values before calculating their euclidean distance. If all entries
     * are equal the kernel is isometric.
     */
    public static GaussianProcess fit(List<RealVector> X, double[] Y, double noise, boolean normalize, double[] lengthScale) {
        if (X.size() < 1)
            throw new IllegalArgumentException("X is empty");
        if (X.size() != Y.length) {
            throw new IllegalArgumentException(MessageFormat.format("X and Y are of different dimensions {0} and {1}", X.size(), Y.length));
        }
        OptionalInt brokenXIndex = IntStream.range(1, X.size()).filter(i -> X.get(i).getDimension() != X.get(i - 1).getDimension()).findFirst();
        brokenXIndex.ifPresent(brokenIndex -> {
            throw new IllegalArgumentException(MessageFormat.format("X has different dimensions at index {0} ({2}) and index {1} ({3})",
                    brokenIndex - 1, brokenXIndex, X.get(brokenIndex - 1).getDimension(), X.get(brokenIndex).getDimension()));
        });
        if (noise <= 0) {
            throw new IllegalArgumentException(MessageFormat.format("noise must be strictly positive, got {0}", noise));
        }
        if (lengthScale.length != X.get(0).getDimension()) {
            throw new IllegalArgumentException(
                    MessageFormat.format("lengthScale is of different dimension {0} than X values {1}", lengthScale.length, X.get(0).getDimension()));
        }
        double mean = 0;
        double standardDeviation = 1;
        if (normalize) {
            mean = StatUtils.mean(Y);
            standardDeviation = Math.sqrt(StatUtils.variance(Y, mean));
            for (int i = 0; i < Y.length; i++) {
                Y[i] = (Y[i] - mean) / standardDeviation;
            }
        }
        RealVector yVector = new ArrayRealVector(Y);
        RealVector scaleV = new ArrayRealVector(lengthScale);

        RealMatrix k = maternKernel(X, scaleV);
        k = k.add(new DiagonalMatrix(DoubleStream.generate(() -> noise).limit(X.size()).toArray(), false));

        CholeskyDecomposition choDec = new CholeskyDecomposition(k);
        RealVector w = choDec.getSolver().solve(yVector);

        return new GaussianProcess(X, w, mean, standardDeviation, choDec, scaleV);
    }

    /**
     * Matern kernel for nu=2.5 (we get a twice differentiable gp)
     */
    private static RealMatrix maternKernel(List<RealVector> X, RealVector lengthScale) {
        RealMatrix k = new Array2DRowRealMatrix(X.size(), X.size());
        for (int i = 0; i < X.size(); i++) {
            RealVector left = X.get(i);
            for (int j = i; j < X.size(); j++) {
                RealVector right = X.get(j);
                double maternVal = maternKernel(left, right, lengthScale);
                k.setEntry(i, j, maternVal);
                k.setEntry(j, i, maternVal);
            }
        }
        return k;
    }

    private static RealVector maternKernel(List<RealVector> X, RealVector v, RealVector lengthScale) {
        RealVector out = new ArrayRealVector(X.size());
        for (int i = 0; i < X.size(); i++) {
            out.setEntry(i, maternKernel(X.get(i), v, lengthScale));
        }
        return out;
    }

    private static double maternKernel(RealVector left, RealVector right, RealVector lengthScale) {
        double dist = left.ebeDivide(lengthScale).getDistance(right.ebeDivide(lengthScale));
        dist *= SQRT_5;
        return (1 + dist) * Math.exp(-dist);
    }

    /**
     * ascii graph for debugging
     */
    public String toString(RealVector min, RealVector max, int width, int height, double minY) {
        char[][] out = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                out[i][j] = ' ';
            }
        }
        double[] mean = new double[width];
        double[] std = new double[width];
        for (int i = 0; i < width; i++) {
            double t = (i + 0.5) / width;
            RealVector x = min.add(max.subtract(min).mapMultiplyToSelf(t));
            double[] meanStd = predict(x);
            mean[i] = meanStd[0];
            std[i] = meanStd[1];
        }
        double[] upperBorder = IntStream.range(0, width).mapToDouble(i -> mean[i] + std[i]).toArray();
        double[] lowerBorder = IntStream.range(0, width).mapToDouble(i -> mean[i] - std[i]).toArray();
        double minVal = Math.max(DoubleStream.of(lowerBorder).min().orElseThrow(), minY);
        double maxVal = DoubleStream.of(upperBorder).max().orElseThrow();
        double range = maxVal - minVal;
        DoubleToIntFunction toCharPos = x -> (int) Math.round(height - height * (x - minVal) / range);
        int[] upperCharPos = DoubleStream.of(upperBorder).mapToInt(toCharPos).toArray();
        int[] lowerCharPos = DoubleStream.of(lowerBorder).mapToInt(toCharPos).toArray();
        int[] mindCharPos = DoubleStream.of(mean).mapToInt(toCharPos).toArray();

        for (int x = 0; x < width; x++) {
            if (upperCharPos[x] > 0 && upperCharPos[x] < height)
                out[upperCharPos[x]][x] = '-';
            if (lowerCharPos[x] > 0 && lowerCharPos[x] < height)
                out[lowerCharPos[x]][x] = '-';
            if (mindCharPos[x] > 0 && mindCharPos[x] < height)
                out[mindCharPos[x]][x] = '+';
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            sb.append(out[i]);
            if (i < 98) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

}
