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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maximizes a function using bayesian optimization.
 */
public class BayesianOptimization {

    private static final Logger logger = LoggerFactory.getLogger(BayesianOptimization.class);

    private static final int MAXIMUM_CONSECUTIVE_RANDOM_PICKS = 3;
    private static final int MAXIMUM_VALID_ACQUISITIONS_PER_ITERATION = 50;
    private static final int MAXIMUM_ACQUISITION_ATTEMPTS_PER_ITERATION = 1000;

    private final RealVector minima;
    private final RealVector maxima;
    private final int maxEvaluations;
    private final int initialPoints;
    private final double noise;
    private final RealVector lengthScale;

    private double consecutiveRandomPicks; // consecutive rounds where the acquisition function yielded no useful improvement

    /**
     * Sets up a bayesian optimization.
     * @param minima of the explored parameters
     * @param maxima of the explored parameters
     * @param initPoints points that are initially sampled for exploration
     * @param maxEvaluations maximal evaluations of the fitted function
     * @param noise of the explored function
     * @param lengthScale width parameter for the matern kernel
     */
    public BayesianOptimization(RealVector minima, RealVector maxima, int initPoints, int maxEvaluations, double noise, RealVector lengthScale) {
        if (minima.getDimension() == 0) {
            throw new IllegalArgumentException("explored parameters must at least have one dimension");
        }
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
        RandomVectorGenerator generator = new HaltonSequenceGenerator(minima.getDimension());
        RealVector size = maxima.subtract(minima);
        return Stream.generate(generator::nextVector).map(haltonPoint -> new ArrayRealVector(haltonPoint).ebeMultiply(size).add(minima));

    }

    private GaussianProcess fit(List<RealVector> inputVectors, List<Double> observations) {
        return GaussianProcess.fit(inputVectors, observations.stream().mapToDouble(Double::doubleValue).toArray(), noise, true,
                lengthScale.toArray());
    }

    private double acquisitionFunction(GaussianProcess gaussianProcess, double[] coordinates, double yMax) {
        // expected improvement
        double[] meanAndStandardDeviation = gaussianProcess.predict(new ArrayRealVector(coordinates));
        double mean = meanAndStandardDeviation[0];
        double standardDeviation = meanAndStandardDeviation[1];
        double neededImprovement = mean - yMax;
        double normalizedNeededImprovement = neededImprovement / standardDeviation;
        NormalDistribution normalDistribution = new NormalDistribution(null, 0, 1);
        return neededImprovement * normalDistribution.cumulativeProbability(normalizedNeededImprovement)
                + standardDeviation * normalDistribution.density(normalizedNeededImprovement);
    }

    private static <T> Optional<T> getNext(Spliterator<T> spliterator) {
        final List<T> result = new ArrayList<>(1);

        if (spliterator.tryAdvance(result::add)) {
            return Optional.of(result.get(0));
        }
        return Optional.empty();
    }

    /**
     * Numerically optimize acquisition function
     */
    private RealVector maximizeAcquisitionFunction(GaussianProcess gaussianProcess, double yMax, Spliterator<RealVector> samples) {
        double bestScore = Double.NEGATIVE_INFINITY;
        double[] bestSolution = getNext(samples).orElseThrow().toArray();
        double[] min = minima.toArray();
        double[] max = maxima.toArray();

        int nonZeroAcquisitions = 0;
        for (int i = 0; i < MAXIMUM_ACQUISITION_ATTEMPTS_PER_ITERATION; i++) {
            if (nonZeroAcquisitions >= MAXIMUM_VALID_ACQUISITIONS_PER_ITERATION) {
                break;
            }
            double[] location = getNext(samples).orElseThrow().toArray();
            if (acquisitionFunction(gaussianProcess, location, yMax) != 0) {
                nonZeroAcquisitions++;
                double acquisitionScore = -BFGS.minimize(coordinates -> -acquisitionFunction(gaussianProcess, coordinates, yMax), 5, location, min,
                        max, 0.00001, 1000);
                // Sometimes result is out of bounds (might be due to numerical errors?)
                for (int j = 0; j < location.length; j++) {
                    location[j] = Math.min(max[j], location[j]);
                    location[j] = Math.max(min[j], location[j]);
                }
                if (acquisitionScore > bestScore) {
                    bestSolution = location;
                    bestScore = acquisitionScore;
                }
            }
        }

        if (nonZeroAcquisitions == 0) {
            consecutiveRandomPicks++;
        } else {
            consecutiveRandomPicks = 0;
        }
        return new ArrayRealVector(bestSolution);
    }

    /**
     * Optimizes a real-valued function and returns a result associated with the optimal value.
     * @param <T> type of the result
     * @param objectiveFunction function to optimize
     * @return result
     */
    public <T> OptimizationResult<T> maximize(Function<RealVector, OptimizationResult<T>> objectiveFunction) {
        List<Double> observations = new ArrayList<>(maxEvaluations);
        List<RealVector> evaluatedPoints = new ArrayList<>(maxEvaluations);
        OptimizationResult<T> best = null;

        // the first couple of executions are reserved for exploration
        sampleSolutionSpace().limit(initialPoints).forEach(evaluatedPoints::add);

        Spliterator<RealVector> candidatePointSampler = sampleSolutionSpace().spliterator();
        consecutiveRandomPicks = 0;

        while (observations.size() < maxEvaluations && consecutiveRandomPicks < MAXIMUM_CONSECUTIVE_RANDOM_PICKS) {
            int idx = observations.size();
            RealVector coordinates;
            if (idx < evaluatedPoints.size()) {
                // hard coded exploration
                coordinates = evaluatedPoints.get(idx);
            } else {
                // GPR
                GaussianProcess gaussianProcess = fit(evaluatedPoints, observations);
                if (logger.isDebugEnabled()) {
                    logger.debug(gaussianProcess.toString(minima, maxima, 100, 25, 0));
                }
                coordinates = maximizeAcquisitionFunction(gaussianProcess, best.score, candidatePointSampler);
                evaluatedPoints.add(coordinates);
            }
            OptimizationResult<T> result = objectiveFunction.apply(coordinates);
            result.params = coordinates;
            observations.add(result.getScore());
            if (best == null || result.score > best.score) {
                best = result;
            }
        }
        return best;
    }

    public static final class OptimizationResult<T> {

        private final double score;
        private final T value;
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
