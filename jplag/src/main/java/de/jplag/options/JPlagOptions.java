package de.jplag.options;

import de.jplag.strategy.ComparisonMode;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

import static de.jplag.strategy.ComparisonMode.NORMAL;

public class JPlagOptions {

    public static final ComparisonMode DEFAULT_COMPARISON_MODE = NORMAL;
    public static final float DEFAULT_SIMILARITY_THRESHOLD = 0;
    public static final int DEFAULT_SHOWN_COMPARISONS = 30;

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * Determines which strategy to use for the comparison of submissions.
     */
    private final ComparisonMode comparisonMode;

    /**
     * If true, submissions that cannot be parsed will be stored in a separate directory.
     */
    private final boolean debugParser;

    /**
     * Array of file suffixes that should be included.
     */
    private final String[] fileSuffixes;

    /**
     * Percentage value (must be between 0 and 100). Comparisons (of submissions pairs) with a similarity below this
     * threshold will be ignored. The default value of 0 allows all matches to be stored. This affects which comparisons are
     * stored and thus make it into the result object.
     *
     * @see JPlagOptions.similarityMetric
     */
    private final float similarityThreshold;

    /**
     * The maximum number of comparisons that will be shown in the generated report. If set to -1 all comparisons will be
     * shown.
     */
    private final int maximumNumberOfComparisons;

    /**
     * The similarity metric determines how the minimum similarity threshold required for a comparison (of two submissions)
     * is calculated. This affects which comparisons are stored and thus make it into the result object.
     *
     * @see JPlagOptions.similarityThreshold
     */
    private final SimilarityMetric similarityMetric;

    /**
     * Tunes the comparison sensitivity by adjusting the minimum token required to be counted as matching section. A smaller
     * <n> increases the sensitivity but might lead to more false-positves.
     */
    private final Integer minimumTokenMatch;

    /**
     * Name of the file that contains the names of files to exclude from comparison.
     */
    private final String exclusionFileName;

    /**
     * Names of the excluded files.
     */
    private final Set<String> excludedFiles;

    /**
     * Directory that contains all submissions.
     */
    private final String rootDirectoryName;

    /**
     * Name of the directory which contains the base code.
     */
    private final String baseCodeSubmissionName;

    /**
     * Example: If the subdirectoryName is 'src', only the code inside submissionDir/src of each submission will be used for
     * comparison.
     */
    private final String subdirectoryName;

    /**
     * Language to use when parsing the submissions.
     */
    private final LanguageOption languageOption;

    /**
     * Level of output verbosity.
     */
    private final Verbosity verbosity;

    private JPlagOptions(ComparisonMode comparisonMode, boolean debugParser, String[] fileSuffixes, float similarityThreshold,
            int maximumNumberOfComparisons, SimilarityMetric similarityMetric, Integer minimumTokenMatch, String exclusionFileName,
            Set<String> excludedFiles, String rootDirectoryName, String baseCodeSubmissionName, String subdirectoryName,
            LanguageOption languageOption, Verbosity verbosity) {
        this.comparisonMode = comparisonMode;
        this.debugParser = debugParser;
        this.fileSuffixes = fileSuffixes;
        this.similarityThreshold = similarityThreshold;
        this.maximumNumberOfComparisons = maximumNumberOfComparisons;
        this.similarityMetric = similarityMetric;
        this.minimumTokenMatch = minimumTokenMatch;
        this.exclusionFileName = exclusionFileName;
        this.excludedFiles = excludedFiles;
        this.rootDirectoryName = rootDirectoryName;
        this.baseCodeSubmissionName = baseCodeSubmissionName;
        this.subdirectoryName = subdirectoryName;
        this.languageOption = languageOption;
        this.verbosity = verbosity;
    }

    public static JPlagOptionsBuilder builder() {
        return new JPlagOptionsBuilder();
    }

    public String getBaseCodeSubmissionName() {
        return baseCodeSubmissionName;
    }

    public ComparisonMode getComparisonMode() {
        return comparisonMode;
    }

    public Set<String> getExcludedFiles() {
        return excludedFiles;
    }

    public String getExclusionFileName() {
        return exclusionFileName;
    }

    public String[] getFileSuffixes() {
        return fileSuffixes;
    }

    public LanguageOption getLanguageOption() {
        return languageOption;
    }

    public int getMaximumNumberOfComparisons() {
        return this.maximumNumberOfComparisons;
    }

    public Integer getMinimumTokenMatch() {
        return minimumTokenMatch;
    }

    public String getRootDirectoryName() {
        return rootDirectoryName;
    }

    public SimilarityMetric getSimilarityMetric() {
        return similarityMetric;
    }

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    public String getSubdirectoryName() {
        return subdirectoryName;
    }

    public Verbosity getVerbosity() {
        return verbosity;
    }

    public boolean hasBaseCode() {
        return this.baseCodeSubmissionName != null;
    }

    public boolean isDebugParser() {
        return debugParser;
    }

    public static class JPlagOptionsBuilder {
        private ComparisonMode comparisonMode = DEFAULT_COMPARISON_MODE;
        private boolean debugParser = false;
        private String[] fileSuffixes;
        private float similarityThreshold = DEFAULT_SIMILARITY_THRESHOLD;
        private int maximumNumberOfComparisons = DEFAULT_SHOWN_COMPARISONS;
        private SimilarityMetric similarityMetric = SimilarityMetric.AVG;
        private Integer minimumTokenMatch;
        private String exclusionFileName;
        private Set<String> excludedFiles = Collections.emptySet();
        private String rootDirectoryName;
        private String baseCodeSubmissionName;
        private String subdirectoryName;
        private LanguageOption languageOption;
        private Verbosity verbosity;

        public JPlagOptionsBuilder setComparisonMode(ComparisonMode comparisonMode) {
            this.comparisonMode = comparisonMode;
            return this;
        }

        public JPlagOptionsBuilder setDebugParser(boolean debugParser) {
            this.debugParser = debugParser;
            return this;
        }

        public JPlagOptionsBuilder setFileSuffixes(String[] fileSuffixes) {
            this.fileSuffixes = fileSuffixes;
            return this;
        }

        public JPlagOptionsBuilder setSimilarityThreshold(float similarityThreshold) {
            if (similarityThreshold > 100) {
                System.out.println("Maximum threshold of 100 used instead of " + similarityThreshold);
                this.similarityThreshold = 100;
            } else if (similarityThreshold < 0) {
                System.out.println("Minimum threshold of 0 used instead of " + similarityThreshold);
                this.similarityThreshold = 0;
            } else {
                this.similarityThreshold = similarityThreshold;
            }
            return this;
        }

        public JPlagOptionsBuilder setMaximumNumberOfComparisons(int maximumNumberOfComparisons) {
            this.maximumNumberOfComparisons = Math.max(maximumNumberOfComparisons, -1);
            return this;
        }

        public JPlagOptionsBuilder setSimilarityMetric(SimilarityMetric similarityMetric) {
            this.similarityMetric = similarityMetric;
            return this;
        }

        public JPlagOptionsBuilder setMinimumTokenMatch(Integer minimumTokenMatch) {
            if (minimumTokenMatch != null && minimumTokenMatch < 1) {
                this.minimumTokenMatch = 1;
            } else {
                this.minimumTokenMatch = minimumTokenMatch;
            }
            return this;
        }

        public JPlagOptionsBuilder setExclusionFileName(String exclusionFileName) {
            this.exclusionFileName = exclusionFileName;
            return this;
        }

        public JPlagOptionsBuilder setExcludedFiles(Set<String> excludedFiles) {
            this.excludedFiles = excludedFiles;
            return this;
        }

        public JPlagOptionsBuilder setRootDirectoryName(String rootDirectoryName) {
            this.rootDirectoryName = rootDirectoryName;
            return this;
        }

        public JPlagOptionsBuilder setBaseCodeSubmissionName(String baseCodeSubmissionName) {
            this.baseCodeSubmissionName = (baseCodeSubmissionName == null) ? null : baseCodeSubmissionName.replace(File.separator, "");
            return this;
        }

        public JPlagOptionsBuilder setSubdirectoryName(String subdirectoryName) {
            // Trim problematic file separators.
            this.subdirectoryName = (subdirectoryName == null) ? null : subdirectoryName.replace(File.separator, "");
            return this;
        }

        public JPlagOptionsBuilder setLanguageOption(LanguageOption languageOption) {
            this.languageOption = languageOption;
            return this;
        }

        public JPlagOptionsBuilder setVerbosity(Verbosity verbosity) {
            this.verbosity = verbosity;
            return this;
        }

        public JPlagOptions build() {
            return new JPlagOptions(comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons, similarityMetric,
                    minimumTokenMatch, exclusionFileName, excludedFiles, rootDirectoryName, baseCodeSubmissionName, subdirectoryName, languageOption,
                    verbosity);
        }
    }
}
