package de.jplag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.clustering.ClusteringFactory;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;
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

    private static Version loadVersion() {
        ResourceBundle versionProperties = ResourceBundle.getBundle("de.jplag.version");
        String versionString = versionProperties.getString("version");
        Version currentVersion = Version.parseVersion(versionString);
        return currentVersion == null ? Version.DEVELOPMENT : currentVersion;
    }

    private final JPlagOptions options;

    /**
     * Creates and initializes a JPlag instance, parameterized by a set of options.
     * @deprecated in favor of static {@link #run(JPlagOptions)}.
     * @param options determines the parameterization.
     */
    @Deprecated(since = "4.3.0")
    public JPlag(JPlagOptions options) {
        this.options = options;
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @deprecated in favor of static {@link #run(JPlagOptions)}.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if JPlag exits preemptively.
     */
    @Deprecated(since = "4.3.0")
    public JPlagResult run() throws ExitException {
        return run(options);
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @param options determines the parameterization.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if JPlag exits preemptively.
     */
    public static JPlagResult run(JPlagOptions options) throws ExitException {
        checkForConfigurationConsistency(options);
        GreedyStringTiling coreAlgorithm = new GreedyStringTiling(options);
        ComparisonStrategy comparisonStrategy = new ParallelComparisonStrategy(options, coreAlgorithm);
        // Parse and validate submissions.
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = JPlag.stripNonCommonTokens(builder.buildSubmissionSet());
        if (options.normalize() && options.language().supportsNormalization() && options.language().requiresCoreNormalization()) {
            submissionSet.normalizeSubmissions();
        }
        int submissionCount = submissionSet.numberOfSubmissions();
        if (submissionCount < 2) {
            throw new SubmissionException("Not enough valid submissions! (found " + submissionCount + " valid submissions)");
        }

        // Compare valid submissions.
        JPlagResult result = comparisonStrategy.compareSubmissions(submissionSet);

        // Use Match Merging against obfuscation
        if (options.mergingOptions().enabled()) {
            result = new MatchMerging(options).mergeMatchesOf(result);
        }

        if (logger.isInfoEnabled()) {
            logger.info("Total time for comparing submissions: {}", TimeUtil.formatDuration(result.getDuration()));
        }
        result.setClusteringResult(ClusteringFactory.getClusterings(result.getAllComparisons(), options.clusteringOptions()));

        logSkippedSubmissions(submissionSet, options);

        return result;
    }

    private static SubmissionSet stripNonCommonTokens(SubmissionSet submissionSet) throws ExitException {
        List<Submission> submissions = submissionSet.getSubmissions();
        Set<Class<?>> tokenContexts = submissions.stream()
                .flatMap(it -> it.getTokenList().stream().map(token -> token.getLanguage().getTokenContexts().stream())).reduce((left, right) -> {
                    return left.filter(it -> right.anyMatch(rightElem -> rightElem.equals(it)));
                }).get().collect(Collectors.toSet());

        List<Submission> strippedSubmissions = submissions.stream().map(sub -> {
            Submission copy = sub.copy();
            copy.setTokenList(sub.getTokenList().stream().filter(it -> tokenContexts.contains(it.getType().getContext())).toList());
            return copy;
        }).toList();

        return new SubmissionSet(strippedSubmissions, submissionSet.getBaseCode(), submissionSet.getOptions());
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

    private static void checkForConfigurationConsistency(JPlagOptions options) throws RootDirectoryException {
        if (options.normalize() && !options.language().supportsNormalization()) {
            logger.error(String.format("The language %s cannot be used with normalization.", options.language().getName()));
        }

        List<String> duplicateNames = getDuplicateSubmissionFolderNames(options);
        if (duplicateNames.size() > 0) {
            throw new RootDirectoryException(String.format("Duplicate root directory names found: %s", String.join(", ", duplicateNames)));
        }
    }

    private static List<String> getDuplicateSubmissionFolderNames(JPlagOptions options) {
        List<String> duplicateNames = new ArrayList<>();
        Set<String> alreadyFoundNames = new HashSet<>();
        for (File file : options.submissionDirectories()) {
            if (!alreadyFoundNames.add(file.getName())) {
                duplicateNames.add(file.getName());
            }
        }
        for (File file : options.oldSubmissionDirectories()) {
            if (!alreadyFoundNames.add(file.getName())) {
                duplicateNames.add(file.getName());
            }
        }
        return duplicateNames;
    }
}
