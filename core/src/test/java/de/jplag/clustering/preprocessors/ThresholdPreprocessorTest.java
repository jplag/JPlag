package de.jplag.clustering.preprocessors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThresholdPreprocessorTest extends PreprocessingTestBase {

    private static final double EPSILON = 0.0000001;

    private ThresholdPreprocessor preprocessor;

    @BeforeEach
    void init() {
        preprocessor = new ThresholdPreprocessor(0.2);
    }

    @Test
    void satisfiesInterface() {
        double[][] original = createTestData();
        double[][] result = preprocessor.preprocessSimilarities(original);
        isValidPreprocessing(original, result, preprocessor::originalIndexOf);
    }

    @Test
    void removedBelowThreshold() {
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
