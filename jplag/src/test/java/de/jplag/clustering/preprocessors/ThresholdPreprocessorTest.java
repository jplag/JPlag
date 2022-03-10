package de.jplag.clustering.preprocessors;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class ThresholdPreprocessorTest extends PreprocessingTestBase {

    private static final double EPSILON = 0.0000001;

    ThresholdPreprocessor preprocessor;

    @Before
    public void init() {
        preprocessor = new ThresholdPreprocessor(0.2);
    }

    @Test
    public void satisfiesInterface() {
        double[][] original = createTestData();
        double[][] result = preprocessor.preprocessSimilarities(original);
        validPreprocessing(original, result, preprocessor::originalIndexOf);
    }

    @Test
    public void removedBelowThreshold() {
        double[][] original = createTestData();
        double[][] result = preprocessor.preprocessSimilarities(original);
        withAllValues(preprocessor, original, result, (originalValue, preprocessed) -> {
            if (originalValue > preprocessor.getThreshold()) {
                assertEquals(Optional.of(originalValue), preprocessed);
            } else {
                assertEquals(0.0, preprocessed.orElse(0.0), EPSILON);
            }
        });
    }

}
