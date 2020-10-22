package jplag;

import static jplag.strategy.ComparisonMode.NORMAL;

import jplag.filter.Filter;
import jplag.options.ClusterType;
import jplag.options.LanguageOption;
import jplag.strategy.ComparisonMode;
import jplag.options.Verbosity;

public class JPlagOptions {

  // List of unidentified options:

  public boolean exp = false; // EXPERIMENT

  public int compare = 0; // 0 = deactivated

  /**
   * This is related to `storeMatches`.
   */
  public static final int MAX_RESULT_PAIRS = 1000;

  /**
   * Related to -s CLI option.
   * <p>
   * Read subdirectories recursively.
   */
  public boolean recursive = false;

  /**
   *
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

  public static void usage() {
    System.out.print(JPlag.name_long
        + ", Copyright (c) 2004-2017 KIT - IPD Tichy, Guido Malpohl, and others.\n"
        + "Usage: JPlag [ options ] <root-dir> [-c file1 file2 ...]\n"
        + " <root-dir>        The root-directory that contains all submissions.\n\n"
        + "options are:\n"
        + " -v[qlpd]        (Verbose)\n"
        + "                 q: (Quiet) no output\n"
        + "                 l: (Long) detailed output\n"
        + "                 p: print all (p)arser messages\n"
        + "                 d: print (d)etails about each submission\n"
        + " -S <dir>        Look in directories <root-dir>/*/<dir> for programs.\n"
        + "                 (default: <root-dir>/*)\n"
        + " -s              (Subdirs) Look at files in subdirs too (default: deactivated)\n\n"
        + " -p <suffixes>   <suffixes> is a comma-separated list of all filename suffixes\n"
        + "                 that are included. (\"-p ?\" for defaults)\n\n"
        + " -o <file>       (Output) The Parserlog will be saved to <file>\n"
        + " -x <file>       (eXclude) All files named in <file> will be ignored\n"
        + " -t <n>          (Token) Tune the sensitivity of the comparison. A smaller\n"
        + "                 <n> increases the sensitivity.\n"
        + " -m <n>          (Matches) Number of matches that will be saved (default:20)\n"
        + " -m <p>%         All matches with more than <p>% similarity will be saved.\n"
        + " -bc <dir>       Name of the directory which contains the basecode (common framework)\n"
        + " -l <language>   (Language) Supported Languages:\n");
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

  // --------------------------------------------------------------------------

  public boolean isRecursive() {
    return recursive;
  }

  public void setRecursive(boolean recursive) {
    this.recursive = recursive;
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
