package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.clustering.Preprocessing;

class ClusteringTest extends CommandLineInterfaceTest {

    private static final double EPSILON = 0.000001;

    @Test
    void parseSkipClustering() {
        String argument = CommandLineArgument.CLUSTER_DISABLE.flag();
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(false, options.clusteringOptions().isEnabled());
    }

    @Test
    void parseDefaultClustering() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(true, options.clusteringOptions().isEnabled());
    }

    @Test
    void parsePercentilePreProcessor() {
        String argument = buildArgument(CommandLineArgument.CLUSTER_PREPROCESSING_PERCENTILE, Float.toString(0.5f));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.PERCENTILE, options.clusteringOptions().getPreprocessor());
        assertEquals(0.5, options.clusteringOptions().getPreprocessorPercentile(), EPSILON);
    }

    @Test
    void parseCdfPreProcessor() {
        String argument = CommandLineArgument.CLUSTER_PREPROCESSING_CDF.flag();
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION, options.clusteringOptions().getPreprocessor());
    }

    @Test
    void parseNoPreProcessor() {
        String argument = CommandLineArgument.CLUSTER_PREPROCESSING_NONE.flag();
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.NONE, options.clusteringOptions().getPreprocessor());
    }

}
