package jplag;

import static jplag.strategy.ComparisonMode.NORMAL;

import jplag.options.ClusterType;
import jplag.options.LanguageOption;
import jplag.strategy.ComparisonMode;
import jplag.options.Verbosity;

public class JPlagOptions {

  /**
   * This is related to `storeMatches`.
   */
  public static final int MAX_RESULT_PAIRS = 1000;

  /**
   * TODO: Decide what to do with this.
   * <p>
   * Note: Previously, this option had two effects:
   * <ol>
   *   <li>If this option was > 0, it told JPlag to use the 'special' comparison strategy</li>
   *   <li>It specifies the number of submissions to compare each submission to during the 'special' comparison</li>
   * </ol>
   */
  public int numberOfSubmissionsToCompareTo = 0; // 0 = deactivated

  /**
   * Clustering option.
   */
  private ClusterType clusterType;

  /**
   * Determines which strategy to use for the comparison of submissions.
   */
  private ComparisonMode comparisonMode = NORMAL;

  /**
   * If true, submissions that cannot be parsed will be stored in a separate directory.
   */
  private boolean debugParser = false;

  /**
   * Array of file suffixes that should be included.
   */
  private String[] fileSuffixes;

  /**
   * TODO: Find a better name for this variable.
   */
  private int storeMatches = 30;

  /**
   * TODO: Find a better name for this variable;
   * <p>
   * True, if `storeMatches` should be interpreted as a percentage; false otherwise;
   */
  private boolean storePercent = false;

  /**
   * Tune the sensitivity of the comparison. A smaller <n> increases the sensitivity
   */
  private Integer minTokenMatch;

  /**
   * Name of the file that contains the names of files to exclude from comparison.
   */
  private String exclusionFileName;

  /**
   * Directory that contains all submissions.
   */
  private String rootDirName;

  /**
   * Name of the directory which contains the base code.
   */
  private String baseCodeSubmissionName;

  /**
   * Example: If the subdirectoryName is 'src', only the code inside submissionDir/src of each
   * submission will be used for comparison.
   */
  private String subdirectoryName;

  /**
   * Language to use when parsing the submissions.
   */
  private LanguageOption languageOption;

  /**
   * Level of output verbosity.
   */
  private Verbosity verbosity;

  public static JPlagOptions fromArgs(String[] args) {
    return new JPlagOptions();
  }

  public void setLanguageDefaults(jplag.Language language) {
    // Set language-specific default options
    if (!this.hasMinTokenMatch()) {
      this.minTokenMatch = language.min_token_match();
    }

    if (!this.hasFileSuffixes()) {
      this.fileSuffixes = language.suffixes();
    }
  }

  public LanguageOption getLanguageOption() {
    return languageOption;
  }

  public Verbosity getVerbosity() {
    return verbosity;
  }

  public boolean hasBaseCode() {
    return this.baseCodeSubmissionName != null;
  }

  public boolean hasFileSuffixes() {
    return this.fileSuffixes != null && this.fileSuffixes.length > 0;
  }

  public boolean hasMinTokenMatch() {
    return this.minTokenMatch != null;
  }

  public ClusterType getClusterType() {
    return clusterType;
  }

  public void setClusterType(ClusterType clusterType) {
    this.clusterType = clusterType;
  }

  public ComparisonMode getComparisonMode() {
    return comparisonMode;
  }

  public void setComparisonMode(ComparisonMode comparisonMode) {
    this.comparisonMode = comparisonMode;
  }

  public String[] getFileSuffixes() {
    return fileSuffixes;
  }

  public void setFileSuffixes(String[] fileSuffixes) {
    this.fileSuffixes = fileSuffixes;
  }

  public int getStoreMatches() {
    return storeMatches;
  }

  public void setStoreMatches(int storeMatches) {
    this.storeMatches = storeMatches;
  }

  public boolean isStorePercent() {
    return storePercent;
  }

  public void setStorePercent(boolean storePercent) {
    this.storePercent = storePercent;
  }

  public Integer getMinTokenMatch() {
    return minTokenMatch;
  }

  public void setMinTokenMatch(Integer minTokenMatch) {
    this.minTokenMatch = minTokenMatch;
  }

  public String getExclusionFileName() {
    return exclusionFileName;
  }

  public void setExclusionFileName(String exclusionFileName) {
    this.exclusionFileName = exclusionFileName;
  }

  public String getRootDirName() {
    return rootDirName;
  }

  public void setRootDirName(String rootDirName) {
    this.rootDirName = rootDirName;
  }

  public String getBaseCodeSubmissionName() {
    return baseCodeSubmissionName;
  }

  public void setBaseCodeSubmissionName(String baseCodeSubmissionName) {
    this.baseCodeSubmissionName = baseCodeSubmissionName;
  }

  public String getSubdirectoryName() {
    return subdirectoryName;
  }

  public void setSubdirectoryName(String subdirectoryName) {
    this.subdirectoryName = subdirectoryName;
  }

  public void setLanguageOption(LanguageOption languageOption) {
    this.languageOption = languageOption;
  }

  public void setVerbosity(Verbosity verbosity) {
    this.verbosity = verbosity;
  }

  public boolean isDebugParser() {
    return debugParser;
  }

  public void setDebugParser(boolean debugParser) {
    this.debugParser = debugParser;
  }
}
