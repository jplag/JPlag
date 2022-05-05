package de.jplag.clustering.preprocessors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PercentilePreprocessorTest extends PreprocessingTestBase {

    private static final double EPSILON = 0.0000001;

    PercentileThresholdProcessor preprocessor;

    @BeforeEach
    public void init() {
        preprocessor = new PercentileThresholdProcessor(0.5f);
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
            // Values are only 0.1 and 0.5; percentile 0.5 should only preserve values of
            // 0.5.
            if (originalValue > 0.1) {
                assertEquals(Optional.of(originalValue), preprocessed);
            } else {
                assertEquals(0.0, preprocessed.orElse(0.0), EPSILON);
            }
        });
    }

}
