package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.clustering.Preprocessing;

class ClusteringTest extends CommandLineInterfaceTest {

    private static final double EPSILON = 0.000001;

    @Test
    void parsePercentilePreProcessor() {
        String argument = CommandLineArgument.CLUSTER_PREPROCESSING_PERCENTILE.flag() + "=" + 0.5f;
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.PERCENTILE, options.getClusteringOptions().getPreprocessor());
        assertEquals(0.5, options.getClusteringOptions().getPreprocessorPercentile(), EPSILON);
    }

    @Test
    void parseCdfPreProcessor() {
        String argument = CommandLineArgument.CLUSTER_PREPROCESSING_CDF.flag();
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION, options.getClusteringOptions().getPreprocessor());
    }

    @Test
    void parseNoPreProcessor() {
        String argument = CommandLineArgument.CLUSTER_PREPROCESSING_NONE.flag();
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.NONE, options.getClusteringOptions().getPreprocessor());
    }

}
