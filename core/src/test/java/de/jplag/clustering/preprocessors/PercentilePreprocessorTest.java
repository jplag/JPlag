package de.jplag.clustering.preprocessors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PercentilePreprocessorTest extends PreprocessingTestBase {

    PercentileThresholdProcessor preprocessor;

    @BeforeEach
    public void init() {
        preprocessor = new PercentileThresholdProcessor(0.5);
    }

    @Test
    public void satisfiesInterface() {
        double[][] original = createTestData();
        double[][] result = preprocessor.preprocessSimilarities(original);
        validPreprocessing(original, result, preprocessor::originalIndexOf);
    }

    @Test
    public void removedBelowPercentile() {
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
