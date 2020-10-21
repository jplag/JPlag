package jplag;

import static jplag.options.Verbosity.LONG;
import static jplag.options.Verbosity.PARSER;
import static jplag.options.Verbosity.QUIET;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import java.util.stream.Collectors;
import jplag.clustering.Clusters;
import jplag.clustering.SimilarityMatrix;
import jplag.options.ClusterType;
import jplag.strategy.ComparisonMode;
import jplag.strategy.ComparisonStrategy;
import jplag.strategy.ExperimentalComparisonStrategy;
import jplag.strategy.ExternalComparisonStrategy;
import jplag.strategy.NormalComparisonStrategy;
import jplag.strategy.RevisionComparisonStrategy;
import jplag.strategy.SpecialComparisonStrategy;
import jplagUtils.PropertiesLoader;

/*
 * This class coordinates the whole program flow.
 * The revision history can be found on https://svn.ipd.kit.edu/trac/jplag/wiki/JPlag/History
 */
public class JPlag implements ProgramI {

  private static final Properties versionProps =
      PropertiesLoader.loadProps("jplag/version.properties");

  public static final String name =
      "JPlag" + versionProps.getProperty("version", "devel");

  public static final String name_long =
      "JPlag (Version " + versionProps.getProperty("version", "devel") + ")";

  /**
   * This stores the name of the submission that is currently parsed.
   * <p>
   * TODO: This should be moved to parseSubmissions(...)
   */
  public String currentSubmissionName = "<Unknown submission>";

  /**
   * Vector of errors that occurred during the execution of the program.
   */
  public Vector<String> errorVector = new Vector<>();

  /**
   * Used Objects of anothers jplag.Classes, they muss be just one time instantiate TODO: What?
   */
  public Clusters clusters = null;

  private int errors = 0;
  private String invalidSubmissionNames = null;

  private Language language;

  public SimilarityMatrix similarity = null;

  /**
   * The base code directory is considered a separate submission.
   */
  private Submission baseCodeSubmission = null;

  /**
   * Comparison strategy to use.
   */
  public ComparisonStrategy comparisonStrategy;

  /**
   * Set of file names to be excluded in comparison.
   */
  private HashSet<String> excludedFileNames = null;

  /**
   * Contains the comparison logic.
   */
  protected GSTiling gSTiling = new GSTiling(this);

  /**
   * JPlag configuration options.
   */
  private final JPlagOptions options;

  /**
   * File writer.
   */
  private FileWriter writer = null;

  public JPlag(JPlagOptions options) throws ExitException {
    this.options = options;

    this.initialize();
  }

  public void initialize() throws ExitException {
    this.initializeLanguage();
    this.initializeComparisonStrategy();
    this.checkBaseCodeOption();
  }

  public void initializeComparisonStrategy() throws ExitException {
    ComparisonMode mode = options.getComparisonMode();

    switch (mode) {
      case NORMAL:
        this.comparisonStrategy = new NormalComparisonStrategy(options, gSTiling);
        return;
      case REVISION:
        this.comparisonStrategy = new RevisionComparisonStrategy(options, gSTiling);
        return;
      case SPECIAL:
        this.comparisonStrategy = new SpecialComparisonStrategy(options, gSTiling);
        return;
      case EXTERNAL:
        this.comparisonStrategy = new ExternalComparisonStrategy(options, gSTiling);
        return;
      case EXPERIMENTAL:
        this.comparisonStrategy = new ExperimentalComparisonStrategy(options, gSTiling);
        return;
      default:
        throw new ExitException("Illegal comparison mode: " + options.getComparisonMode());
    }
  }

  public void initializeLanguage() throws ExitException {
    jplag.options.Language language = this.options.getLanguage();

    try {
      Class<?> languageClass = Class.forName(language.getClassPath());
      Constructor<?>[] languageConstructors = languageClass.getDeclaredConstructors();

      // TODO: Verify that only one constructor exists
      Constructor<?> constructor = languageConstructors[0];
      Object[] constructorParams = {this};

      this.language = (jplag.Language) constructor.newInstance(constructorParams);
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
      e.printStackTrace();

      throw new ExitException("Language instantiation failed", ExitException.BAD_LANGUAGE_ERROR);
    }

    this.options.setLanguageDefaults(this.language);

    System.out.println("Initialized language " + this.language.name());
  }

  /**
   * This method checks whether the base code directory value is valid.
   */
  private void checkBaseCodeOption() throws ExitException {
    if (!this.options.hasBaseCode()) {
      return;
    }

    String baseCodePath =
        this.options.getRootDirName() + File.separator + this.options.getBaseCode();

    if (!(new File(this.options.getRootDirName())).exists()) {
      throw new ExitException(
          "Root directory \"" + this.options.getRootDirName() + "\" doesn't exist!",
          ExitException.BAD_PARAMETER
      );
    }

    File f = new File(baseCodePath);

    if (!f.exists()) {
      // Base code dir doesn't exist
      throw new ExitException("Basecode directory \"" + baseCodePath
          + "\" doesn't exist!", ExitException.BAD_PARAMETER);
    }

    if (this.options.getSubDir() != null && this.options.getSubDir().length() != 0) {
      f = new File(baseCodePath, this.options.getSubDir());

      if (!f.exists()) {
        throw new ExitException(
            "Basecode directory doesn't contain" + " the subdirectory \"" + this.options
                .getSubDir() + "\"!",
            ExitException.BAD_PARAMETER
        );
      }
    }

    System.out.println("Basecode directory \"" + baseCodePath + "\" will be used");
  }

  public JPlagOptions getOptions() {
    return this.options;
  }

  /**
   * Main procedure
   */
  public JPlagResult run() throws ExitException {
    File rootDir = new File(options.getRootDirName());

    if (!rootDir.exists()) {
      throw new ExitException("Root directory " + options.getRootDirName() + " does not exist!");
    }

    if (!rootDir.isDirectory()) {
      throw new ExitException(options.getRootDirName() + " is not a directory!");
    }

    // This file contains all files names which are excluded
    readExclusionFile();

    Vector<Submission> submissions = findSubmissions(rootDir);

    if (options.isSkipParse()) {
      print("Skipping parsing...\n", null);
    } else {
      parseAllSubmissions(submissions, baseCodeSubmission);
    }

    submissions = filterValidSubmissions(submissions);

    if (submissions.size() < 2) {
      printErrors();
      throw new ExitException(
          "Not enough valid submissions! (found " + submissions.size() + " valid submissions)",
          ExitException.NOT_ENOUGH_SUBMISSIONS_ERROR
      );
    }

    // errorVector is not needed anymore
    errorVector = null;

    if (options.getClusterType() != ClusterType.NONE) {
      clusters = new Clusters(this);
      similarity = new SimilarityMatrix(submissions.size());
    }

    System.gc();

    JPlagResult result = comparisonStrategy.compareSubmissions(submissions, baseCodeSubmission);

    closeWriter();

    return result;
  }

  private Vector<Submission> filterValidSubmissions(Vector<Submission> submissions) {
    return submissions.stream()
        .filter(submission -> !submission.errors)
        .collect(Collectors.toCollection(Vector::new));
  }

  /**
   * TODO: Find a better way to separate parseSubmissions(...) and parseBaseCodeSubmission(...)
   */
  public void parseAllSubmissions(Vector<Submission> submissions, Submission baseCodeSubmission)
      throws ExitException {
    try {
      parseSubmissions(submissions);
      System.gc();
      parseBaseCodeSubmission(baseCodeSubmission);
    } catch (OutOfMemoryError e) {
      System.gc();

      throw new ExitException(
          "Out of memory during parsing of submission \"" + currentSubmissionName + "\"");
    } catch (Throwable e) {
      e.printStackTrace();

      throw new ExitException(
          "Unknown exception during parsing of " + "submission \"" + currentSubmissionName
              + "\"");
    }
  }

  /**
   * Add an error to the errorVector.
   */
  public void addError(String errorMsg) {
    errorVector.add("[" + currentSubmissionName + "]\n" + errorMsg);
    print(errorMsg, null);
  }

  /**
   * Print all errors from the errorVector.
   */
  private void printErrors() {
    StringBuilder errorStr = new StringBuilder();

    for (String str : errorVector) {
      errorStr.append(str);
      errorStr.append('\n');
    }

    System.out.println(errorStr.toString());
  }

  public void print(String normal, String lng) {
    if (options.getVerbosity() == PARSER) {
      if (lng != null) {
        myWrite(lng);
      } else if (normal != null) {
        myWrite(normal);
      }
    }
    if (options.getVerbosity() == QUIET) {
      return;
    }
    try {
      if (normal != null) {
        System.out.print(normal);
      }

      if (lng != null) {
        if (options.getVerbosity() == LONG) {
          System.out.print(lng);
        }
      }
    } catch (Throwable e) {
      System.out.println(e.getMessage());
    }
  }

  public void closeWriter() {
    try {
      if (writer != null) {
        writer.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    writer = null;
  }

  public boolean hasValidSuffix(File file) {
    boolean hasValidSuffix = false;
    String name = file.getName();

    for (String suffix : options.getFileSuffixes()) {
      if (name.endsWith(suffix)) {
        hasValidSuffix = true;
        break;
      }
    }

    return hasValidSuffix;
  }

  /**
   * Find all submissions in the given root directory.
   */
  private Vector<Submission> findSubmissions(File rootDir) throws jplag.ExitException {
    Vector<Submission> submissions = new Vector<>();

    String[] fileNamesInRootDir;

    try {
      fileNamesInRootDir = rootDir.list();
    } catch (SecurityException e) {
      throw new jplag.ExitException(
          "Unable to retrieve directory: " + options.getRootDirName() + " Cause : " + e.toString());
    }

    if (fileNamesInRootDir == null) {
      return submissions;
    }

    Arrays.sort(fileNamesInRootDir);

    for (String fileName : fileNamesInRootDir) {
      File submissionFile = new File(rootDir, fileName);

      if (isFileExcluded(submissionFile)) {
        System.out.println("Exclude submission: " + submissionFile.getName());
        continue;
      }

      Submission submission;

      if (submissionFile.isDirectory()) {
        if (options.getSubDir() != null) {
          submissionFile = new File(submissionFile, options.getSubDir());
        }

        if (!submissionFile.exists()) {
          throw new ExitException(
              String.format(
                  "Submission %s does not contain a subdirectory called '%s'",
                  fileName,
                  options.getSubDir()
              )
          );
        }

        if (!submissionFile.isDirectory()) {
          throw new ExitException(
              String.format(
                  "The given subdirectory '%s' is not a directory!",
                  options.getSubDir()
              )
          );
        }

        submission = new Submission(
            fileName,
            submissionFile,
            options.isRecursive(),
            this,
            this.language
        );
      } else {
        // If subDir option is set, a submission can't be a single file -> ignore.
        if (options.getSubDir() != null) {
          continue;
        }

        // Ignore single-file submissions with an invalid file suffix.
        if (!hasValidSuffix(submissionFile)) {
          continue;
        }

        submission = new Submission(fileName, rootDir, this, this.language);
      }

      if (options.getBaseCode().equals(fileName)) {
        baseCodeSubmission = submission;
      } else {
        submissions.addElement(submission);
      }
    }

    return submissions;
  }

  /**
   * Check if a file is excluded or not.
   */
  protected boolean isFileExcluded(File file) {
    if (excludedFileNames == null) {
      return false;
    }

    String fileName = file.getName();

    for (String s : excludedFileNames) {
      if (fileName.endsWith(s)) {
        return true;
      }
    }

    return false;
  }

  private void makeTempDir() throws jplag.ExitException {
    print(null, "Creating temporary dir.\n");
    File f = new File("temp");
    if (!f.exists()) {
      if (!f.mkdirs()) {
        throw new jplag.ExitException("Cannot create temporary directory!");
      }
    }
    if (!f.isDirectory()) {
      throw new ExitException("'temp' is not a directory!");
    }
    if (!f.canWrite()) {
      throw new ExitException("Cannot write directory: 'temp'");
    }
  }

  private void myWrite(String str) {
    if (writer != null) {
      try {
        writer.write(str);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.print(str);
    }
  }

  /**
   * Parse all given submissions.
   */
  private void parseSubmissions(Vector<Submission> submissions) throws jplag.ExitException {
    if (submissions == null) {
      System.out.println("Nothing to parse!");
      return;
    }

    int count = 0;

    long msec = System.currentTimeMillis();
    Iterator<Submission> iter = submissions.iterator();

    if (options.getComparisonMode() == ComparisonMode.EXTERNAL) {
      makeTempDir();
    }

    int invalid = 0;
    while (iter.hasNext()) {
      boolean ok;
      boolean removed = false;
      Submission subm = iter.next();

      print(null, "------ Parsing submission: " + subm.name + "\n");
      currentSubmissionName = subm.name;

      if (!(ok = subm.parse())) {
        errors++;
      }

      if (options.exp && options.getFilter() != null) {
        subm.struct = options.getFilter().filter(subm.struct); // EXPERIMENT
      }

      count++;

      if (subm.struct != null && subm.size() < options.getMinTokenMatch()) {
        print(null, "Submission contains fewer tokens than minimum match " + "length allows!\n");
        subm.struct = null;
        invalid++;
        removed = true;
      }

      if (options.getComparisonMode() == ComparisonMode.EXTERNAL) {
        if (subm.struct != null) {
          this.gSTiling.create_hashes(subm.struct, options.getMinTokenMatch(), false);
          subm.struct.save(new File("temp", subm.dir.getName() + subm.name));
          subm.struct = null;
        }
      }

      if (options.getComparisonMode() != ComparisonMode.EXTERNAL && subm.struct == null) {
        invalidSubmissionNames = (invalidSubmissionNames == null) ? subm.name
            : invalidSubmissionNames + " - " + subm.name;
        iter.remove();
      }

      if (ok && !removed) {
        print(null, "OK\n");
      } else {
        print(null, "ERROR -> Submission removed\n");
      }
    }

    print("\n" + (count - errors - invalid) + " submissions parsed successfully!\n" + errors
        + " parser error"
        + (errors != 1 ? "s!\n" : "!\n"), null);

    if (invalid != 0) {
      print(null, invalid
          + ((invalid == 1) ? " submission is not valid because it contains"
          : " submissions are not valid because they contain")
          + " fewer tokens\nthan minimum match length allows.\n");
    }

    long time = System.currentTimeMillis() - msec;

    print("\n\n",
        "\nTotal time for parsing: " + ((time / 3600000 > 0) ? (time / 3600000) + " h " : "")
            + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
            + " sec\n"
            + "Time per parsed submission: " + (count > 0 ? (time / count) : "n/a") + " msec\n\n");
  }

  /**
   * Parse the given base code submission.
   */
  private void parseBaseCodeSubmission(Submission subm) throws jplag.ExitException {
    if (subm == null) {
      // TODO:
      // options.useBasecode = false;
      return;
    }

    long msec = System.currentTimeMillis();
    print("----- Parsing basecode submission: " + subm.name + "\n", null);

    // lets go:
    if (options.getComparisonMode() == ComparisonMode.EXTERNAL) {
      makeTempDir();
    }

    if (!subm.parse()) {
      printErrors();
      throw new ExitException("Bad basecode submission");
    }

    if (options.exp && options.getFilter() != null) {
      subm.struct = options.getFilter().filter(subm.struct); // EXPERIMENT
    }

    if (subm.struct != null && subm.size() < options.getMinTokenMatch()) {
      throw new ExitException(
          "Basecode submission contains fewer tokens " + "than minimum match length allows!\n");
    }

    if (options.hasBaseCode()) {
      gSTiling.create_hashes(subm.struct, options.getMinTokenMatch(), true);
    }

    if (options.getComparisonMode() == ComparisonMode.EXTERNAL) {
      if (subm.struct != null) {
        gSTiling.create_hashes(subm.struct, options.getMinTokenMatch(), false);
        subm.struct.save(new File("temp", subm.dir.getName() + subm.name));
        subm.struct = null;
      }
    }

    print("\nBasecode submission parsed!\n", null);

    long time = System.currentTimeMillis() - msec;

    print("\n",
        "\nTime for parsing Basecode: " + ((time / 3600000 > 0) ? (time / 3600000) + " h " : "")
            + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
            + " sec\n");
  }

  /*
   * If an exclusion file is given, it is read in and all stings are saved in
   * the set "excluded".
   */
  private void readExclusionFile() {
    if (options.getExcludeFile() == null) {
      return;
    }

    excludedFileNames = new HashSet<>();

    try {
      BufferedReader in = new BufferedReader(new FileReader(options.getExcludeFile()));
      String line;

      while ((line = in.readLine()) != null) {
        excludedFileNames.add(line.trim());
      }

      in.close();
    } catch (IOException e) {
      System.out.println("Could not read exclusion file: " + options.getExcludeFile());
    }

    if (options.getVerbosity() == LONG) {
      print(null, "Excluded files:\n");

      for (String excludedFileName : excludedFileNames) {
        print(null, "  " + excludedFileName + "\n");
      }
    }
  }
}
