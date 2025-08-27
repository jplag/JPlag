package de.jplag.clustering.preprocessors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PercentilePreprocessorTest extends PreprocessingTestBase {

    private PercentileThresholdProcessor preprocessor;

    @BeforeEach
    void init() {
        preprocessor = new PercentileThresholdProcessor(0.5);
    }

    @Test
    void satisfiesInterface() {
        double[][] original = createTestData();
        double[][] result = preprocessor.preprocessSimilarities(original);
        isValidPreprocessing(original, result, preprocessor::originalIndexOf);
    }

    @Test
    void removedBelowPercentile() {
        double[][] original = createTestData();
        double[][] result = preprocessor.preprocessSimilarities(original);
        withAllValues(preprocessor, original, result, (originalValue, preprocessed) -> {
            // Median is 0.1 => Values >= 0.1 should preserved.
            if (originalValue >= 0.1) {
                assertEquals(Optional.of(originalValue), preprocessed);
            } else {
                assertTrue(preprocessed.isEmpty());
            }
        });
    }

}
