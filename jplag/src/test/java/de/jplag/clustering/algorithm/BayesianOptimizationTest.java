package de.jplag.clustering.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;

class BayesianOptimizationTest {

    @Test
    void findParabolaMax() {
        RealVector minima = new ArrayRealVector(new double[] {-20});
        RealVector maxima = new ArrayRealVector(new double[] {10});
        RealVector lengthScale = new ArrayRealVector(new double[] {5});
        double maximumAt = 1;
        BayesianOptimization bo = new BayesianOptimization(minima, maxima, 3, 15, 1.0 / 12.0, lengthScale);
        double maximumPoint = bo.maximize(v -> {
            double val = v.getEntry(0);
            double result = -(val - maximumAt) * (val - maximumAt);
            result += 0.2 * (Math.random() - 0.5);
            return new BayesianOptimization.OptimizationResult<>(result, result);
        }).getParams().getEntry(0);
        assertEquals(maximumAt, maximumPoint, 1);
    }
}
