package de.jplag.clustering.algorithm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;

class GaussianProcessTest {

    @Test
    void noisyLinearFunction() {
        List<RealVector> inputVectors = new ArrayList<>();
        double[] targets = new double[20];
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            RealVector x = new ArrayRealVector(1);
            x.setEntry(0, i);
            inputVectors.add(x);
            inputVectors.add(x);
            targets[idx++] = i + Math.random() - 0.5;
            targets[idx++] = i + Math.random() - 0.5;
        }
        GaussianProcess gp = GaussianProcess.fit(inputVectors, targets, 1 / 12.0, true, new double[] {1});
        for (int i = 0; i < 19; i++) {
            RealVector vx = new ArrayRealVector(1);
            double x = i / 2.0;
            vx.setEntry(0, x);
            double[] prediction = gp.predict(vx);
            assertTrue(Math.abs(prediction[0] - x) < 1, "The prediction error can't very high");
            assertTrue(prediction[1] > 0, "The standard deviation must be greater than 0");
        }
    }
}
