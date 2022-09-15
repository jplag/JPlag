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

    private final List<RealVector> listOfCoordinates;
    private final RealVector weight;
    private final double mean;
    private final double standardDeviation;
    private final CholeskyDecomposition cholesky;
    private final RealVector lengthScale;

    private GaussianProcess(List<RealVector> listOfCoordinates, RealVector weight, double mean, double standardDeviation,
            CholeskyDecomposition cholesky, RealVector lengthScale) {
        this.listOfCoordinates = listOfCoordinates;
        this.weight = weight;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.cholesky = cholesky;
        this.lengthScale = lengthScale;
    }

    /**
     * @param coordinates coordinate to predict at
     * @return array containing the predicted [mean, standard deviation] at x.
     */
    public double[] predict(RealVector coordinates) {
        RealVector kernelizedCoordinates = maternKernel(listOfCoordinates, coordinates, lengthScale);
        RealVector kernelMatrixTimesCoordinates = cholesky.getSolver().solve(kernelizedCoordinates);

        double predictedMean = weight.dotProduct(kernelizedCoordinates);
        double predictedStandardDeviation = Math
                .sqrt(maternKernel(coordinates, coordinates, lengthScale) - kernelMatrixTimesCoordinates.dotProduct(kernelizedCoordinates));

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
     * @param observedCoordinates TODO DOCUMENTATION MISSING
     * @param observations expected to have zero mean, unit variance if normalize is false
     * @param noise variance of noise in Y
     * @param normalize if Y should be normalized
     * @param lengthScale X values are divided by these values before calculating their euclidean distance. If all entries
     * are equal the kernel is isometric.
     */
    public static GaussianProcess fit(List<RealVector> observedCoordinates, double[] observations, double noise, boolean normalize,
            double[] lengthScale) {
        if (observedCoordinates.isEmpty())
            throw new IllegalArgumentException("Observed coordinates are empty");
        if (observedCoordinates.size() != observations.length) {
            throw new IllegalArgumentException(MessageFormat.format("Observed coordinates and observations are of different dimensions {0} and {1}",
                    observedCoordinates.size(), observations.length));
        }
        OptionalInt brokenXIndex = IntStream.range(1, observedCoordinates.size())
                .filter(i -> observedCoordinates.get(i).getDimension() != observedCoordinates.get(i - 1).getDimension()).findFirst();
        brokenXIndex.ifPresent(brokenIndex -> {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Observed coordinates has different dimensions at index {0} ({2}) and index {1} ({3})", brokenIndex - 1, brokenXIndex,
                    observedCoordinates.get(brokenIndex - 1).getDimension(), observedCoordinates.get(brokenIndex).getDimension()));
        });
        if (noise <= 0) {
            throw new IllegalArgumentException(MessageFormat.format("noise must be strictly positive, got {0}", noise));
        }
        if (lengthScale.length != observedCoordinates.get(0).getDimension()) {
            throw new IllegalArgumentException(MessageFormat.format("lengthScale is of different dimension {0} than the coordinates values {1}",
                    lengthScale.length, observedCoordinates.get(0).getDimension()));
        }
        double mean = 0;
        double standardDeviation = 1;
        if (normalize) {
            mean = StatUtils.mean(observations);
            standardDeviation = Math.sqrt(StatUtils.variance(observations, mean));
            for (int i = 0; i < observations.length; i++) {
                observations[i] = (observations[i] - mean) / standardDeviation;
            }
        }
        RealVector observationVector = new ArrayRealVector(observations);
        RealVector lengthScaleVector = new ArrayRealVector(lengthScale);

        RealMatrix k = maternKernel(observedCoordinates, lengthScaleVector);
        k = k.add(new DiagonalMatrix(DoubleStream.generate(() -> noise).limit(observedCoordinates.size()).toArray(), false));

        CholeskyDecomposition decomposition = new CholeskyDecomposition(k);
        RealVector w = decomposition.getSolver().solve(observationVector);

        return new GaussianProcess(observedCoordinates, w, mean, standardDeviation, decomposition, lengthScaleVector);
    }

    /**
     * Matern kernel for nu=2.5 (we get a twice differentiable gp)
     */
    private static RealMatrix maternKernel(List<RealVector> observedCoordinates, RealVector lengthScale) {
        RealMatrix k = new Array2DRowRealMatrix(observedCoordinates.size(), observedCoordinates.size());
        for (int i = 0; i < observedCoordinates.size(); i++) {
            RealVector left = observedCoordinates.get(i);
            for (int j = i; j < observedCoordinates.size(); j++) {
                RealVector right = observedCoordinates.get(j);
                double maternVal = maternKernel(left, right, lengthScale);
                k.setEntry(i, j, maternVal);
                k.setEntry(j, i, maternVal);
            }
        }
        return k;
    }

    private static RealVector maternKernel(List<RealVector> observedCoordinates, RealVector vector, RealVector lengthScale) {
        RealVector out = new ArrayRealVector(observedCoordinates.size());
        for (int i = 0; i < observedCoordinates.size(); i++) {
            out.setEntry(i, maternKernel(observedCoordinates.get(i), vector, lengthScale));
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
            RealVector coordinates = min.add(max.subtract(min).mapMultiplyToSelf(t));
            double[] meanStd = predict(coordinates);
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

        for (int i = 0; i < width; i++) {
            if (upperCharPos[i] > 0 && upperCharPos[i] < height)
                out[upperCharPos[i]][i] = '-';
            if (lowerCharPos[i] > 0 && lowerCharPos[i] < height)
                out[lowerCharPos[i]][i] = '-';
            if (mindCharPos[i] > 0 && mindCharPos[i] < height)
                out[mindCharPos[i]][i] = '+';
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < height; i++) {
            stringBuilder.append(out[i]);
            if (i < 98) {
                stringBuilder.append('\n');
            }
        }
        return stringBuilder.toString();
    }

}
