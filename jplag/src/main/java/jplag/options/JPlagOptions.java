package jplag.options;

import static jplag.options.JPlagComparisonMode.NORMAL;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import jplag.ExitException;
import jplag.JPlag;
import jplag.clustering.SimilarityMatrix;
import jplag.filter.Filter;

public class JPlagOptions {

  // --------------------------------------------------------------------------

  // List of unidentified options:

  public boolean exp = false; // EXPERIMENT

  public int compare = 0; // 0 = deactivated

  // TODO: I think this should be an attribute of Program.java
  public SimilarityMatrix similarity = null;

  // --------------------------------------------------------------------------

  // Copied from CommandLineOptions.java

  public void initializeSecondStep(JPlag program) throws ExitException {
    try {
      Constructor<?>[] languageConstructors = Class.forName(language.getClassName())
          .getDeclaredConstructors();
      Constructor<?> cons = languageConstructors[0];
      Object[] ob = {program};
      // All Language have to have a program as Constructor Parameter
      // -> public Language(ProgramI prog)
      this.languageInstance = (jplag.Language) cons.newInstance(ob);
      System.out.println("Language accepted: " + languageInstance.name());
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      System.out.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();

      throw new ExitException(
          "Illegal value: Language instantiation failed",
          ExitException.BAD_LANGUAGE_ERROR
      );
    }

    // Set language-specific defaults:
    if (!hasMinTokenMatch()) {
      this.minTokenMatch = this.languageInstance.min_token_match();
    }

    if (!hasFileSuffixes()) {
      this.fileSuffixes = this.languageInstance.suffixes();
    }

    checkBaseCodeOption();
  }

  /**
   * This method checks whether the basecode directory value is valid
   */
  private void checkBaseCodeOption() throws ExitException {
    if (hasBaseCode()) {
      if (baseCode == null || baseCode.equals("")) {
        throw new ExitException(
            "Base code option used but none specified!",
            ExitException.BAD_PARAMETER
        );
      }

      String baseC = rootDir + File.separator + baseCode;

      if (!(new File(rootDir)).exists()) {
        throw new ExitException(
            "Root directory \"" + rootDir + "\" doesn't exist!",
            ExitException.BAD_PARAMETER
        );
      }
      File f = new File(baseC);

      if (!f.exists()) {
        // Base code dir doesn't exist
        throw new ExitException("Basecode directory \"" + baseC
            + "\" doesn't exist!", ExitException.BAD_PARAMETER);
      }

      if (subDir != null && subDir.length() != 0) {
        f = new File(baseC, subDir);

        if (!f.exists()) {
          throw new ExitException(
              "Basecode directory doesn't contain" + " the subdirectory \"" + subDir + "\"!",
              ExitException.BAD_PARAMETER
          );
        }
      }

      System.out.println("Basecode directory \"" + baseC + "\" will be used");
    }
  }

  // --------------------------------------------------------------------------

  /**
   * This is related to `storeMatches`.
   */
  public static final int MAX_RESULT_PAIRS = 1000;

  /**
   * TODO: No idea how this works.
   * <p>
   * Clustering threshold.
   */
  private float[] threshold = null;

  /**
   * TODO: What does this mean?
   * <p>
   * Generate a special "diff" report.
   */
  private boolean useDiffReport = false;

  /**
   * TODO: What does this mean?
   */
  private boolean useExternalSearch = false;

  /**
   * Related to -s CLI option.
   * <p>
   * Read subdirectories recursively.
   */
  public boolean recursive = false;

  /**
   * TODO: What does this mean?
   */
  private boolean skipParse = false;

  /**
   * List of files to compare.
   */
  private List<String> fileList;

  /**
   * Clustering option.
   */
  private JPlagClusterType clusterType;

  /**
   * TODO: Don't know what the title is used for.
   */
  private String title = "";

  /**
   * Determine which mode to use for the comparison of submissions.
   */
  private JPlagComparisonMode comparisonMode = NORMAL;

  /**
   * TODO: Don't know what this is used for. Seems to be experimental only.
   */
  private Filter filter = null;

  /**
   * Array of file suffixes that should be included.
   */
  private String[] fileSuffixes;

  /**
   * Directory in which the web pages will be stored.
   */
  private String resultDir = "result";

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
   * TODO: No idea what this is for.
   */
  private String includeFile = null;

  /**
   * // TODO: Should be a list. Files with this name will be ignored.
   */
  private String excludeFile;

  /**
   * Directory that contains all submissions.
   */
  private String rootDir;

  /**
   * Directory to store non-parsable files.
   */
  private String originalDir;

  /**
   * True, if the debug parser should be used; false otherwise.
   */
  private boolean useDebugParser;

  /**
   * Name of the directory which contains the base code.
   */
  private String baseCode;

  /**
   * Name of the file to save the logger output to.
   */
  private String outputFile;

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
  private Language language;

  /**
   * The actual language instance used by JPlag to process the submissions.
   */
  private jplag.Language languageInstance;

  /**
   * Level of output verbosity.
   */
  private Verbosity verbosity;

  public static JPlagOptions fromArgs(String[] args) {
    return new JPlagOptions();
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
        + " -d              (Debug) parser. Non-parsable files will be stored.\n"
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
        + " -r <dir>        (Result) Name of directory in which the web pages will be\n"
        + "                 stored (default: result)\n"
        + " -bc <dir>       Name of the directory which contains the basecode (common framework)\n"
        + " -c [files]      Compare a list of files. Should be the last one.\n"
        + " -l <language>   (Language) Supported Languages:\n                 ");
  }

  public Language getLanguage() {
    return language;
  }

  public Verbosity getVerbosity() {
    return verbosity;
  }

  public boolean hasBaseCode() {
    return this.baseCode != null;
  }

  public boolean hasFileList() {
    return this.fileList != null && this.fileList.size() > 0;
  }

  public boolean hasFileSuffixes() {
    return this.fileSuffixes != null && this.fileSuffixes.length > 0;
  }

  public boolean hasMinTokenMatch() {
    return this.minTokenMatch != null;
  }

  // --------------------------------------------------------------------------

  // GETTERS

  public float[] getThreshold() {
    return threshold;
  }

  public boolean isUseDiffReport() {
    return useDiffReport;
  }

  public boolean isUseExternalSearch() {
    return useExternalSearch;
  }

  public boolean isRecursive() {
    return recursive;
  }

  public boolean isSkipParse() {
    return skipParse;
  }

  public List<String> getFileList() {
    return fileList;
  }

  public JPlagClusterType getClusterType() {
    return clusterType;
  }

  public String getTitle() {
    return title;
  }

  public JPlagComparisonMode getComparisonMode() {
    return comparisonMode;
  }

  public Filter getFilter() {
    return filter;
  }

  public String[] getFileSuffixes() {
    return fileSuffixes;
  }

  public String getResultDir() {
    return resultDir;
  }

  public int getStoreMatches() {
    return storeMatches;
  }

  public boolean isStorePercent() {
    return storePercent;
  }

  public Integer getMinTokenMatch() {
    return minTokenMatch;
  }

  public String getIncludeFile() {
    return includeFile;
  }

  public String getExcludeFile() {
    return excludeFile;
  }

  public String getRootDir() {
    return rootDir;
  }

  public String getOriginalDir() {
    return originalDir;
  }

  public boolean isUseDebugParser() {
    return useDebugParser;
  }

  public String getBaseCode() {
    return baseCode;
  }

  public String getOutputFile() {
    return outputFile;
  }

  public String getSubDir() {
    return subDir;
  }

  public jplag.Language getLanguageInstance() {
    return languageInstance;
  }
}
