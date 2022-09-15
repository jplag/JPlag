package de.jplag.clustering.preprocessors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CumulativeDistributionFunctionPreprocessorTest extends PreprocessingTestBase {

    private static final double EPSILON = 0.0000001;

    CumulativeDistributionFunctionPreprocessor preprocessor;

    @BeforeEach
    public void init() {
        preprocessor = new CumulativeDistributionFunctionPreprocessor();
    }

    @Test
    public void satisfiesInterface() {
        double[][] original = createTestData();
        double[][] result = preprocessor.preprocessSimilarities(original);
        validPreprocessing(original, result, preprocessor::originalIndexOf);
    }

    @Test
    public void mayDecreaseOnly() {
        double[][] original = createTestData();
        double[][] result = preprocessor.preprocessSimilarities(original);
        withAllValues(preprocessor, original, result, (originalValue, preprocessed) -> {
            if (originalValue == 0.0) {
                assertEquals(0.0, preprocessed.orElse(0.0), EPSILON);
            } else {
                assertTrue(preprocessed.get() <= originalValue);
            }
        });
    }

}
