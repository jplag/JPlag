package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.jplag.clustering.Preprocessing;

class ClusteringTest extends CommandLineInterfaceTest {

    @Test
    void parseSkipClustering() throws CliException {
        buildOptionsFromCLI(defaultArguments().skipClustering());
        assertFalse(options.clusteringOptions().enabled());
    }

    @Test
    void parseDefaultClustering() throws CliException {
        buildOptionsFromCLI(defaultArguments());
        assertTrue(options.clusteringOptions().enabled());
    }

    @Test
    void parsePercentilePreProcessor() throws CliException {
        buildOptionsFromCLI(defaultArguments().clusterPpPercentile(.5));
        assertEquals(Preprocessing.PERCENTILE, options.clusteringOptions().preprocessor());
        assertEquals(0.5, options.clusteringOptions().preprocessorPercentile());
    }

    @Test
    void parseCdfPreProcessor() throws CliException {
        buildOptionsFromCLI(defaultArguments().clusterPpCdf());
        assertEquals(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION, options.clusteringOptions().preprocessor());
    }

    @Test
    void parseNoPreProcessor() throws CliException {
        buildOptionsFromCLI(defaultArguments().clusterPpNone());
        assertEquals(Preprocessing.NONE, options.clusteringOptions().preprocessor());
    }

}
