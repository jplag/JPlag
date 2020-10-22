package jplag;

import static jplag.strategy.ComparisonMode.NORMAL;

import jplag.filter.Filter;
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
   * TODO: Check how this option was set in the previous version.
   */
  private boolean skipParse = false;

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
   * TODO: Don't know what this is used for. Seems to be experimental only.
   */
  private Filter filter = null;

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
   * // TODO: Should be a list. Files with this name will be ignored.
   */
  private String excludeFile;

  /**
   * Directory that contains all submissions.
   */
  private String rootDirName;

  /**
   * Name of the directory which contains the base code.
   */
  private String baseCode;

  /**
   * Name of the directory in which to look for submissions.
   * <p>
   * Seems to refer to a subdirectory in the base code.
   */
  private String subDir;

  /**
   * TODO: Rename (conflicts with jplag.Language) Selected language to process the submissions
   * with.
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
    return this.baseCode != null;
  }

  public boolean hasFileSuffixes() {
    return this.fileSuffixes != null && this.fileSuffixes.length > 0;
  }

  public boolean hasMinTokenMatch() {
    return this.minTokenMatch != null;
  }

  public boolean isSkipParse() {
    return skipParse;
  }

  public void setSkipParse(boolean skipParse) {
    this.skipParse = skipParse;
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

  public Filter getFilter() {
    return filter;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
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

  public String getExcludeFile() {
    return excludeFile;
  }

  public void setExcludeFile(String excludeFile) {
    this.excludeFile = excludeFile;
  }

  public String getRootDirName() {
    return rootDirName;
  }

  public void setRootDirName(String rootDirName) {
    this.rootDirName = rootDirName;
  }

  public String getBaseCode() {
    return baseCode;
  }

  public void setBaseCode(String baseCode) {
    this.baseCode = baseCode;
  }

  public String getSubDir() {
    return subDir;
  }

  public void setSubDir(String subDir) {
    this.subDir = subDir;
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
