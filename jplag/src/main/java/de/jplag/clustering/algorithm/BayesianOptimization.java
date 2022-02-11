package de.jplag.clustering.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.HaltonSequenceGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;

public class BayesianOptimization {

    private static final int STOP_AFTER_CONSECUTIVE_RANDOM_PICKS = 3;
    private static final int MAX_NON_ZERO_ACQ_FN_EVALS_PER_ITERATION = 50;
    private static final int MAXIMUM_ACQ_FN_EVALS_PER_ITERATION = 1000;

    private RealVector minima;
    private RealVector maxima;
    private int maxEvaluations;
    private int initialPoints;
    private double noise;
    private RealVector lengthScale;
    public boolean debug = false;

    /**
     * @param minima of the explored parameters
     * @param maxima of the explored parameters
     * @param initPoints points that are initially sampled for exploration
     * @param maxEvaluations maximal evaluations of the fitted function
     * @param noise of the explored function
     * @param lengthScale width parameter for the matern kernel
     */
    public BayesianOptimization(RealVector minima, RealVector maxima, int initPoints, int maxEvaluations, double noise, RealVector lengthScale) {
        if (minima.getDimension() != maxima.getDimension()) {
            throw new DimensionMismatchException(minima.getDimension(), maxima.getDimension());
        }
        if (initPoints < 1 || initPoints > maxEvaluations) {
            throw new OutOfRangeException(initPoints, 1, maxEvaluations);
        }
        this.maxima = maxima;
        this.minima = minima;
        this.initialPoints = initPoints;
        this.maxEvaluations = maxEvaluations;
        this.noise = noise;
        this.lengthScale = lengthScale;
    }

    private Stream<RealVector> sampleSolutionSpace() {
        RandomVectorGenerator g = new HaltonSequenceGenerator(minima.getDimension());
        RealVector size = maxima.subtract(minima);
        return Stream.generate(g::nextVector).map(x -> new ArrayRealVector(x)).map(x -> x.ebeMultiply(size)).map(x -> x.add(minima));

    }

    private GaussianProcess fit(List<RealVector> X, List<Double> Y) {
        return GaussianProcess.fit(X, Y.stream().mapToDouble(Double::doubleValue).toArray(), noise, true, lengthScale.toArray());
    }

    private double acquisitionFunction(GaussianProcess gpr, double[] r, double yMax) {
        // expected improvement
        double[] meanStd = gpr.predictWidthStd(new ArrayRealVector(r));
        double mean = meanStd[0];
        double std = meanStd[1];
        double a = mean - yMax;
        double z = a / std;
        NormalDistribution norm = new NormalDistribution(null, 0, 1);
        return a * norm.cumulativeProbability(z) + std * norm.density(z);
    }

    private static <T> Optional<T> getNext(Spliterator<T> spliterator) {
        final List<T> result = new ArrayList<>(1);

        if (spliterator.tryAdvance(result::add)) {
            return Optional.of(result.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Numerically optimize acquisition function
     */
    private RealVector maxAcq(GaussianProcess gpr, double yMax, Spliterator<RealVector> samples, double[] randomPicks) {
        double bestScore = Double.NEGATIVE_INFINITY;
        double[] bestSolution = getNext(samples).orElseThrow().toArray();
        double[] min = minima.toArray();
        double[] max = maxima.toArray();

        int nonZeroAcquisitions = 0;
        for (int i = 0; i < MAXIMUM_ACQ_FN_EVALS_PER_ITERATION && nonZeroAcquisitions < MAX_NON_ZERO_ACQ_FN_EVALS_PER_ITERATION; i++) {
            double[] r = getNext(samples).orElseThrow().toArray();
            if (acquisitionFunction(gpr, r, yMax) == 0)
                continue;
            nonZeroAcquisitions++;
            double poi = -BFGS.minimize(x -> -acquisitionFunction(gpr, x, yMax), 5, r, min, max, 0.00001, 1000);
            // Sometimes result is out of bounds (might be due to numerical errors?)
            for (int j = 0; j < r.length; j++) {
                r[j] = Math.min(max[j], r[j]);
                r[j] = Math.max(min[j], r[j]);
            }
            if (poi > bestScore) {
                bestSolution = r;
                bestScore = poi;
            }
        }
        if (nonZeroAcquisitions == 0) {
            randomPicks[0]++;
        } else {
            randomPicks[0] = 0;
        }
        return new ArrayRealVector(bestSolution);
    }

    /**
     * Optimizes a real-valued function and returns a result associated with the optimal value.
     * 
     * @param <T> type of the result
     * @param f function to optimize
     * @return result
     */
    public <T> OptimizationResult<T> maximize(Function<RealVector, OptimizationResult<T>> f) {
        List<Double> Y = new ArrayList<>(maxEvaluations);
        List<RealVector> X = new ArrayList<>(maxEvaluations);
        OptimizationResult<T> best = null;

        // the first couple of executions are reserved for exploration
        sampleSolutionSpace().limit(initialPoints).forEach(X::add);

        Spliterator<RealVector> poiSampler = sampleSolutionSpace().spliterator();
        double[] zeroAcquisitionsCounter = new double[1];

        while (Y.size() < maxEvaluations && zeroAcquisitionsCounter[0] < STOP_AFTER_CONSECUTIVE_RANDOM_PICKS) {
            int idx = Y.size();
            RealVector x;
            if (idx < X.size()) {
                // hard coded exploration
                x = X.get(idx);
            } else {
                // GPR
                GaussianProcess gpr = fit(X, Y);
                if (debug) {
                    System.out.println(gpr.toString(minima, maxima, 100, 25, 0));
                }
                x = maxAcq(gpr, best.score, poiSampler, zeroAcquisitionsCounter);
                X.add(x);
            }
            OptimizationResult<T> result = f.apply(x);
            result.params = x;
            Y.add(result.getScore());
            if (best == null) {
                best = result;
            } else if (result.score > best.score) {
                best = result;
            }
        }

        return best;
    }

    public static final class OptimizationResult<T> {

        private double score;
        private T value;
        private RealVector params;

        public OptimizationResult(double score, T value) {
            this.score = score;
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public double getScore() {
            return score;
        }

        public RealVector getParams() {
            return params;
        }
    }
}
