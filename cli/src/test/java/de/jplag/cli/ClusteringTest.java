package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.clustering.Preprocessing;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class ClusteringTest extends CliTest {
    private static final double CLUSTERING_TEST_PERCENTILE = .5;

    @Test
    void parseSkipClustering() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SKIP_CLUSTERING));
        assertFalse(options.clusteringOptions().enabled());
    }

    @Test
    void parseDefaultClustering() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions();
        assertTrue(options.clusteringOptions().enabled());
    }

    @Test
    void parsePercentilePreProcessor() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.CLUSTER_PP_PERCENTILE, CLUSTERING_TEST_PERCENTILE));

        assertEquals(Preprocessing.PERCENTILE, options.clusteringOptions().preprocessor());
        assertEquals(CLUSTERING_TEST_PERCENTILE, options.clusteringOptions().preprocessorPercentile());
    }

    @Test
    void parseCdfPreProcessor() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.CLUSTER_PP_CDF));
        assertEquals(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION, options.clusteringOptions().preprocessor());
    }

    @Test
    void parseNoPreProcessor() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.CLUSTER_PP_NONE));
        assertEquals(Preprocessing.NONE, options.clusteringOptions().preprocessor());
    }
}
