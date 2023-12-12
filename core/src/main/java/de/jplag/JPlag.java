package de.jplag;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.clustering.ClusteringFactory;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.merging.MatchMerging;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.model.Version;
import de.jplag.strategy.ComparisonStrategy;
import de.jplag.strategy.ParallelComparisonStrategy;

/**
 * This class coordinates the whole errorConsumer flow.
 */
public class JPlag {
    private static final Logger logger = LoggerFactory.getLogger(JPlag.class);

    public static final Version JPLAG_VERSION = loadVersion();

    private final UiHooks uiHooks;

    private static Version loadVersion() {
        ResourceBundle versionProperties = ResourceBundle.getBundle("de.jplag.version");
        String versionString = versionProperties.getString("version");
        Version currentVersion = Version.parseVersion(versionString);
        return currentVersion == null ? Version.DEVELOPMENT : currentVersion;
    }

    private final JPlagOptions options;

    /**
     * Creates and initializes a JPlag instance, parameterized by a set of options.
     * @param options determines the parameterization.
     */
    public JPlag(JPlagOptions options) {
        this(options, UiHooks.NullUiHooks);
    }

    /**
     * Creates and initializes a JPlag instance, parameterized by a set of options.
     * @param options determines the parameterization.
     * @param uiHooks Used to notify the ui of state changes
     */
    public JPlag(JPlagOptions options, UiHooks uiHooks) {
        this.options = options;
        this.uiHooks = uiHooks;
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @param options determines the parameterization.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if JPlag exits preemptively.
     */
    public static JPlagResult run(JPlagOptions options) throws ExitException {
        return new JPlag(options).run();
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if JPlag exits preemptively.
     */
    public JPlagResult run() throws ExitException {
        GreedyStringTiling coreAlgorithm = new GreedyStringTiling(options);
        ComparisonStrategy comparisonStrategy = new ParallelComparisonStrategy(options, coreAlgorithm);
        // Parse and validate submissions.
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet(this.uiHooks);
        int submissionCount = submissionSet.numberOfSubmissions();
        if (submissionCount < 2)
            throw new SubmissionException("Not enough valid submissions! (found " + submissionCount + " valid submissions)");

        // Compare valid submissions.
        JPlagResult result = comparisonStrategy.compareSubmissions(submissionSet, this.uiHooks);

        // Use Match Merging against obfuscation
        if (options.mergingOptions().enabled()) {
            result = new MatchMerging(options).mergeMatchesOf(result);
        }

        if (logger.isInfoEnabled())
            logger.info("Total time for comparing submissions: {}", TimeUtil.formatDuration(result.getDuration()));
        result.setClusteringResult(ClusteringFactory.getClusterings(result.getAllComparisons(), options.clusteringOptions()));

        logSkippedSubmissions(submissionSet, options);

        return result;
    }

    private static void logSkippedSubmissions(SubmissionSet submissionSet, JPlagOptions options) {
        List<Submission> skippedSubmissions = submissionSet.getInvalidSubmissions();
        if (!skippedSubmissions.isEmpty()) {
            logger.warn("{} submissions were skipped (see errors above): {}", skippedSubmissions.size(), skippedSubmissions);
            if (options.debugParser()) {
                logger.warn("Erroneous submissions were copied to {}", new File(JPlagOptions.ERROR_FOLDER).getAbsolutePath());
            }
        }
    }
}
