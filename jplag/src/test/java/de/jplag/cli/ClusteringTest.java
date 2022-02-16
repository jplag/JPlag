package de.jplag.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jplag.CommandLineArgument;
import de.jplag.clustering.Preprocessors;

public class ClusteringTest extends CommandLineInterfaceTest {

    private static final double EPSILON = 0.000001;

    @Test
    public void parsePercentilePreProcessor() {
        String argument = CommandLineArgument.CLUSTER_PREPROCESSING_PERCENTILE.flag() + "=" + Float.toString(0.5f);
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessors.PERCENTILE, options.getClusteringOptions().getPreprocessor());
        assertEquals(0.5, options.getClusteringOptions().getPreprocessorPercentile(), EPSILON);
    }

    @Test
    public void parseCdfPreProcessor() {
        String argument = CommandLineArgument.CLUSTER_PREPROCESSING_CDF.flag();
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessors.CDF, options.getClusteringOptions().getPreprocessor());
    }

    @Test
    public void parseNoPreProcessor() {
        String argument = CommandLineArgument.CLUSTER_PREPROCESSING_NONE.flag();
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessors.NONE, options.getClusteringOptions().getPreprocessor());
    }

}
