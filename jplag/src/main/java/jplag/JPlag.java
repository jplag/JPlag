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
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

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

  public String currentSubmissionName = "<Unknown submission>";

  public Vector<String> errorVector = new Vector<>();

  // Used Objects of anothers jplag.Classes ,they muss be just one time
  // instantiate
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
   * Vector of file name to be included in comparison.
   */
  private Vector<String> includedFileNames = null;

  /**
   * Contains the comparison logic.
   */
  protected GSTiling gSTiling = new GSTiling(this);

  /**
   * JPlag configuration options.
   */
  private final JPlagOptions options;

  /**
   * TODO: Check whether only valid submissions should be stored here.
   * <p>
   * Vector of (valid?)submissions.
   */
  private Vector<Submission> submissions;

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

    String baseCodePath = this.options.getRootDir() + File.separator + this.options.getBaseCode();

    if (!(new File(this.options.getRootDir())).exists()) {
      throw new ExitException(
          "Root directory \"" + this.options.getRootDir() + "\" doesn't exist!",
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
  public JPlagResult run() throws jplag.ExitException {
    // This file contains all files names which are excluded
    readExclusionFile();

    if (options.hasFileList()) {
      createSubmissionsFileList();
    } else if (options.getIncludeFile() == null) {
      createSubmissions();
      System.out.println(submissions.size() + " submissions");
    } else {
      createSubmissionsExp();
    }

    if (!options.isSkipParse()) {
      doParse();
    } else {
      print("Skipping parsing...\n", null);
    }

    if (countValidSubmissions() < 2) {
      throwNotEnoughSubmissions();
    }

    errorVector = null; // errorVector is not needed anymore

    if (options.getClusterType() != ClusterType.NONE) {
      clusters = new Clusters(this);
      similarity = new SimilarityMatrix(submissions.size());
    }

    System.gc();

    JPlagResult result = comparisonStrategy.compareSubmissions(submissions, baseCodeSubmission);

    closeWriter();

    return result;
  }

  /**
   * All submission with no errors are counted. (unsure if this is still necessary.)
   */
  protected int countValidSubmissions() {
    if (submissions == null) {
      return 0;
    }
    int size = 0;
    for (int i = submissions.size() - 1; i >= 0; i--) {
      if (!submissions.elementAt(i).errors) {
        size++;
      }
    }
    return size;
  }

  public void doParse() throws ExitException {
    try {
      parseAll();
      System.gc();
      parseBaseCodeSubmission();
    } catch (OutOfMemoryError e) {
      submissions = null;
      System.gc();
      System.out.println(
          "[" + new Date() + "] OutOfMemoryError " + "during parsing of submission \""
              + currentSubmissionName
              + "\"");
      throw new ExitException(
          "Out of memory during parsing of submission \"" + currentSubmissionName + "\"");
    } catch (ExitException e) {
      throw e;
    } catch (Throwable e) {
      System.out.println(
          "[" + new Date() + "] Unknown exception " + "during parsing of submission \""
              + currentSubmissionName
              + "\"");
      e.printStackTrace();
      throw new ExitException(
          "Unknown exception during parsing of " + "submission \"" + currentSubmissionName
              + "\"");
    }
  }

  public void addError(String errorMsg) {
    errorVector.add("[" + currentSubmissionName + "]\n" + errorMsg);
    print(errorMsg, null);
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

  private void throwNotEnoughSubmissions() throws jplag.ExitException {
    StringBuilder errorStr = new StringBuilder();
    for (String str : errorVector) {
      errorStr.append(str);
      errorStr.append('\n');
    }

    throw new ExitException("Not enough valid submissions! (only " + countValidSubmissions() + " "
        + (countValidSubmissions() != 1 ? "are" : "is") + " valid):\n" + errorStr.toString(),
        ExitException.NOT_ENOUGH_SUBMISSIONS_ERROR);
  }

  private void throwBadBaseCodeSubmission() throws jplag.ExitException {
    StringBuilder errorStr = new StringBuilder();
    for (String str : errorVector) {
      errorStr.append(str);
      errorStr.append('\n');
    }

    throw new ExitException("Bad basecode submission:\n" + errorStr.toString());
  }

  /**
   * Parse all submissions in the given `options.rootDir`. This method is called from
   * Program.run(...).
   */
  private void createSubmissions() throws jplag.ExitException {
    submissions = new Vector<>();

    // ------------------------------------------------------------------------

    File rootDir = new File(options.getRootDir());

    if (!rootDir.isDirectory()) {
      throw new jplag.ExitException("\"" + options.getResultDir() + "\" is not a directory!");
    }

    // ------------------------------------------------------------------------

    String[] fileNamesInRootDir;

    try {
      fileNamesInRootDir = rootDir.list();
    } catch (SecurityException e) {
      throw new jplag.ExitException(
          "Unable to retrieve directory: " + options.getRootDir() + " Cause : " + e.toString());
    }

    Arrays.sort(fileNamesInRootDir);

    // ------------------------------------------------------------------------

    for (String fileName : fileNamesInRootDir) {
      File subm_dir = new File(rootDir, fileName);

      if (!subm_dir.isDirectory()) {
        // If subDir option is set, a submission can't be a single file -> ignore.
        if (options.getSubDir() != null) {
          continue;
        }

        boolean hasValidSuffix = false;
        String name = subm_dir.getName();

        // Make sure the single-file submission has a valid suffix
        for (String suffix : options.getFileSuffixes()) {
          if (name.endsWith(suffix)) {
            hasValidSuffix = true;
            break;
          }
        }

        // Ignore single-file submissions with an invalid file suffix.
        if (!hasValidSuffix) {
          continue;
        }

        submissions.addElement(new Submission(name, rootDir, this, this.language));

      } else if (options.exp && isFileExcluded(subm_dir.toString())) {
        // EXPERIMENT !!
        System.err.println("excluded: " + subm_dir);

      } else {
        File file_dir = ((options.getSubDir() == null) ?
            subm_dir : new File(subm_dir, options.getSubDir()));

        if (file_dir.isDirectory()) {
          if (options.getBaseCode().equals(subm_dir.getName())) {
            baseCodeSubmission = new Submission(
                subm_dir.getName(),
                file_dir,
                options.isRecursive(),
                this,
                this.language
            );
          } else {
            submissions.addElement(new Submission(
                subm_dir.getName(),
                file_dir,
                options.isRecursive(),
                this,
                this.language
            ));
          }
        } else {
          throw new ExitException("Cannot find directory: " + file_dir.toString());
        }
      }
    }
  }

  /**
   * Parse submissions from the given `options.fileList` only. This method is called from
   * Program.run(...).
   */
  private void createSubmissionsFileList() throws jplag.ExitException {
    submissions = new Vector<>();

    File rootDir = null;

    if (options.getRootDir() != null) {
      rootDir = new File(options.getRootDir());

      if (!rootDir.isDirectory()) {
        throw new jplag.ExitException(options.getRootDir() + " is not a directory!");
      }
    }

    for (String file : options.getFileList()) {
      submissions.addElement(new Submission(file, rootDir, this, options.getLanguageInstance()));
    }
  }


  /**
   * THIS IS FOR THE EMPIRICAL STUDY
   */
  private void createSubmissionsExp() throws jplag.ExitException {
    // ES IST SICHER, DASS EIN INCLUDE-FILE ANGEGEBEN WURDE!
    readIncludeFile();

    submissions = new Vector<>();

    // ------------------------------------------------------------------------

    File rootDir = new File(options.getRootDir());

    if (!rootDir.isDirectory()) {
      throw new jplag.ExitException(options.getRootDir() + " is not a directory!");
    }

    // ------------------------------------------------------------------------

    String[] list = new String[includedFileNames.size()];
    includedFileNames.copyInto(list);

    // ------------------------------------------------------------------------

    for (String s : list) {
      File submissionDir = new File(rootDir, s);

      if (!submissionDir.isDirectory()) {
        continue;
      }

      if (options.exp && isFileExcluded(submissionDir.toString())) { // EXPERIMENT
        System.err.println("excluded: " + submissionDir);
        continue;
      }

      File file_dir = ((options.getSubDir() == null) ? submissionDir
          : new File(submissionDir, options.getSubDir()));

      if (file_dir.isDirectory()) {
        submissions.addElement(new Submission(
            submissionDir.getName(),
            file_dir,
            options.isRecursive(),
            this,
            this.options.getLanguageInstance()
        ));
      } else if (options.getSubDir() == null) {
        throw new ExitException(options.getRootDir() + " is not a directory!");
      }
    }
  }

  /**
   * Check if a file is excluded or not.
   */
  protected boolean isFileExcluded(String file) {
    if (excludedFileNames == null) {
      return false;
    }

    for (String s : excludedFileNames) {
      if (file.endsWith(s)) {
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

  /*
   * Compiles all "submissions"
   */
  private void parseAll() throws jplag.ExitException {
    if (submissions == null) {
      System.out.println("Nothing to parse!");
      return;
    }

    // lets go:)
    int count = 0;
    int totalcount = submissions.size();

    long msec = System.currentTimeMillis();
    Iterator<Submission> iter = submissions.iterator();

    if (options.isUseExternalSearch()) {
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

      if (options.isUseExternalSearch()) {
        if (subm.struct != null) {
          this.gSTiling.create_hashes(subm.struct, options.getMinTokenMatch(), false);
          subm.struct.save(new File("temp", subm.dir.getName() + subm.name));
          subm.struct = null;
        }
      }

      if (!options.isUseExternalSearch() && subm.struct == null) {
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

  private void parseBaseCodeSubmission() throws jplag.ExitException {
    Submission subm = baseCodeSubmission;

    if (subm == null) {
      // TODO:
      // options.useBasecode = false;
      return;
    }

    long msec = System.currentTimeMillis();
    print("----- Parsing basecode submission: " + subm.name + "\n", null);

    // lets go:
    if (options.isUseExternalSearch()) {
      makeTempDir();
    }

    if (!subm.parse()) {
      throwBadBaseCodeSubmission();
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

    if (options.isUseExternalSearch()) {
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

  /*
   * If an include file is given, read it in and store all the strings in
   * "included".
   */
  private void readIncludeFile() {
    if (options.getIncludeFile() == null) {
      return;
    }

    includedFileNames = new Vector<>();

    try {
      BufferedReader in = new BufferedReader(new FileReader(options.getIncludeFile()));
      String line;

      while ((line = in.readLine()) != null) {
        includedFileNames.addElement(line.trim());
      }

      in.close();
    } catch (IOException e) {
      System.out.println("Could not read include file: " + options.getIncludeFile());
    }

    if (options.getVerbosity() == LONG) {
      print(null, "Included dirs:\n");

      Enumeration<String> enum1 = includedFileNames.elements();

      while (enum1.hasMoreElements()) {
        print(null, "  " + enum1.nextElement() + "\n");
      }
    }
  }
}
