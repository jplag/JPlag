package jplag;

import static jplag.options.Verbosity.LONG;
import static jplag.options.Verbosity.PARSER;
import static jplag.options.Verbosity.QUIET;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import jplag.clustering.Cluster;
import jplag.clustering.Clusters;
import jplag.clustering.SimilarityMatrix;
import jplag.options.ClusterType;
import jplagUtils.PropertiesLoader;

/*
 * This class coordinates the whole program flow.
 * The revision history can be found on https://svn.ipd.kit.edu/trac/jplag/wiki/JPlag/History
 */
public class JPlag implements ProgramI {

  private static final Properties versionProps = PropertiesLoader
      .loadProps("jplag/version.properties");
  public static final String name = "JPlag" + versionProps.getProperty("version", "devel");
  public static final String name_long =
      "JPlag (Version " + versionProps.getProperty("version", "devel") + ")";

  public String currentSubmissionName = "<Unknown submission>";
  public Vector<String> errorVector = new Vector<>();

  private Submission baseCodeSubmission = null;

  // Used Objects of anothers jplag.Classes ,they muss be just one time
  // instantiate
  public Clusters clusters = null;

  private int errors = 0;
  private String invalidSubmissionNames = null;

  /**
   * Set of file names to be excluded in comparison.
   */
  private HashSet<String> excludedFileNames = null;

  /**
   * Vector of file name to be included in comparison.
   */
  private Vector<String> includedFileNames = null;

  protected GSTiling gSTiling = new GSTiling(this);

  private Hashtable<String, AllBasecodeMatches> htBasecodeMatches = new Hashtable<>(30);

  // experiment end

  private final JPlagOptions options;

  private final Runtime runtime = Runtime.getRuntime();

  private Vector<Submission> submissions;

  private FileWriter writer = null;

  public JPlag(JPlagOptions options) throws ExitException {
    this.options = options;

    this.options.initializeSecondStep(this);
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
      options.similarity = new SimilarityMatrix(submissions.size());
    }

    System.gc();

    switch (options.getComparisonMode()) {
      case NORMAL:
        compare();
        break;
      case REVISION:
        revisionCompare();
        break;
      case SPECIAL:
        specialCompare();
        break;
      case EXTERNAL:
        try {
          externalCompare();
        } catch (OutOfMemoryError e) {
          e.printStackTrace();
        }
        break;
      case EXPERIMENTAL:
        expCompare();
        break;
      default:
        throw new ExitException(
            "Illegal comparison mode: \"" + options.getComparisonMode() + "\""
        );
    }

    closeWriter();

    // TODO: Return an actual result.
    return new JPlagResult();
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
   * Now the actual comparison: All submissions are compared pairwise.
   */
  private void compare() {
    int size = submissions.size();

    // Result vectors
    SortedVector<AllMatches> avgMatches = new SortedVector<>(new AllMatches.AvgComparator());
    SortedVector<AllMatches> maxMatches = new SortedVector<>(new AllMatches.MaxComparator());
    int[] dist = new int[10];

    long msec;

    AllBasecodeMatches bcMatch;
    Submission s1, s2;

    // ------------------------------------------------------------------------

    if (this.options.hasBaseCode()) {
      int countBC = 0;
      msec = System.currentTimeMillis();

      for (int i = 0; i < (size); i++) {
        s1 = submissions.elementAt(i);

        bcMatch = this.gSTiling.compareWithBasecode(s1, baseCodeSubmission);
        htBasecodeMatches.put(s1.name, bcMatch);
        this.gSTiling.resetBaseSubmission(baseCodeSubmission);

        countBC++;
      }

      long timebc = System.currentTimeMillis() - msec;

      print("\n\n",
          "\nTime for comparing with Basecode: " + ((timebc / 3600000 > 0) ? (timebc / 3600000)
              + " h " : "")
              + ((timebc / 60000 > 0) ? ((timebc / 60000) % 60000) + " min " : "") + (timebc / 1000
              % 60) + " sec\n"
              + "Time per basecode comparison: " + (timebc / size) + " msec\n\n");
    }

    // ------------------------------------------------------------------------

    int totalComps = (size - 1) * size / 2;
    int i, j, anz = 0, count = 0;
    AllMatches match;

    msec = System.currentTimeMillis();

    for (i = 0; i < (size - 1); i++) {
      s1 = submissions.elementAt(i);
      if (s1.struct == null) {
        count += (size - i - 1);
        continue;
      }

      for (j = (i + 1); j < size; j++) {
        s2 = submissions.elementAt(j);
        if (s2.struct == null) {
          count++;
          continue;
        }

        match = this.gSTiling.compare(s1, s2);

        anz++;

        System.out.println("Comparing " + s1.name + "-" + s2.name + ": " + match.percent());

        // histogram:
        if (options.hasBaseCode()) {
          match.bcmatchesA = htBasecodeMatches.get(match.subA.name);
          match.bcmatchesB = htBasecodeMatches.get(match.subB.name);
        }

        registerMatch(match, dist, avgMatches, maxMatches, null, i, j);
        count++;
      }
    }

    long time = System.currentTimeMillis() - msec;

    print("\n",
        "Total time for comparing submissions: " + ((time / 3600000 > 0) ? (time / 3600000) + " h "
            : "")
            + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
            + " sec\n" + "Time per comparison: "
            + (time / anz) + " msec\n");

    // ------------------------------------------------------------------------

    Cluster cluster = null;

    if (options.getClusterType() != ClusterType.NONE) {
      cluster = this.clusters.calculateClustering(submissions);
    }

    // Deprecated:
    // writeResults(dist, avgmatches, maxmatches, null, cluster);
    // TODO: Replace writeResults(...)
  }

  /**
   * Revision compare mode: Compare each submission only with its next submission.
   */
  private void revisionCompare() {
    int size = submissions.size();

    // Result vectors
    SortedVector<AllMatches> avgmatches = new SortedVector<>(
        new AllMatches.AvgReversedComparator());
    SortedVector<AllMatches> maxmatches = new SortedVector<>(
        new AllMatches.MaxReversedComparator());
    SortedVector<AllMatches> minmatches = new SortedVector<>(
        new AllMatches.MinReversedComparator());
    int[] dist = new int[10];

    long msec;

    AllBasecodeMatches bcmatch;
    Submission s1, s2;

    // ------------------------------------------------------------------------

    if (options.hasBaseCode()) {
      msec = System.currentTimeMillis();

      for (int i = 0; i < size; i++) {
        s1 = submissions.elementAt(i);
        bcmatch = gSTiling.compareWithBasecode(s1, baseCodeSubmission);

        htBasecodeMatches.put(s1.name, bcmatch);
        gSTiling.resetBaseSubmission(baseCodeSubmission);
      }

      long timebc = System.currentTimeMillis() - msec;

      print("\n\n",
          "\nTime for comparing with Basecode: " + ((timebc / 3600000 > 0) ? (timebc / 3600000)
              + " h " : "")
              + ((timebc / 60000 > 0) ? ((timebc / 60000) % 60000) + " min " : "") + (timebc / 1000
              % 60) + " sec\n"
              + "Time per basecode comparison: " + (timebc / size) + " msec\n\n");
    }

    // ------------------------------------------------------------------------

    int totalcomps = size - 1;
    int anz = 0, count = 0;
    AllMatches match;

    msec = System.currentTimeMillis();

    s1loop:
    for (int i = 0; i < size - 1; ) {
      s1 = submissions.elementAt(i);
      if (s1.struct == null) {
        count++;
        continue;
      }

      // Find next valid submission
      int j = i;
      do {
        j++;
        if (j >= size) {
          break s1loop; // no more comparison pairs available
        }
        s2 = submissions.elementAt(j);
      } while (s2.struct == null);

      match = this.gSTiling.compare(s1, s2);

      anz++;

      /*
       * System.out.println("Comparing "+s1.name+"-"+s2.name+": "+
       * match.percent());
       */
      // histogram:
      if (options.hasBaseCode()) {
        match.bcmatchesA = htBasecodeMatches.get(match.subA.name);
        match.bcmatchesB = htBasecodeMatches.get(match.subB.name);
      }

      registerMatch(match, dist, avgmatches, maxmatches, minmatches, i, j);
      count++;

      i = j;
    }

    long time = System.currentTimeMillis() - msec;

    print("\n",
        "Total time for comparing submissions: " + ((time / 3600000 > 0) ? (time / 3600000) + " h "
            : "")
            + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
            + " sec\n" + "Time per comparison: "
            + (time / anz) + " msec\n");

    // ------------------------------------------------------------------------

    Cluster cluster = null;

    if (options.getClusterType() != ClusterType.NONE) {
      cluster = this.clusters.calculateClustering(submissions);
    }

    // Deprecated:
    // writeResults(dist, avgmatches, maxmatches, minmatches, cluster);
    // TODO: Replace writeResults(...)
  }

  // EXPERIMENT !!!!! special compare routine!
  private void expCompare() {
    int size = countValidSubmissions();
    int[] similarity = new int[(size * size - size) / 2];

    int anzSub = submissions.size();
    int i, j, count = 0;
    Submission s1, s2;
    AllMatches match;
    long msec = System.currentTimeMillis();

    for (i = 0; i < (anzSub - 1); i++) {
      s1 = submissions.elementAt(i);
      if (s1.struct == null) {
        continue;
      }
      for (j = (i + 1); j < anzSub; j++) {
        s2 = submissions.elementAt(j);
        if (s2.struct == null) {
          continue;
        }

        match = this.gSTiling.compare(s1, s2);
        similarity[count++] = (int) match.percent();
      }
    }

    long time = System.currentTimeMillis() - msec;

    // output
    System.out.print(options.getRootDir() + " ");
    System.out.print(options.getMinTokenMatch() + " ");
    System.out.print(options.getFilter() + " ");
    System.out.print((time) + " ");

    for (i = 0; i < similarity.length; i++) {
      System.out.print(similarity[i] + " ");
    }

    System.out.println();
  }

  /**
   * This is the special external comparison routine
   */
  private void externalCompare() {
    int size = submissions.size();

    // Result vector
    SortedVector<AllMatches> avgmatches = new SortedVector<>(
        new AllMatches.AvgComparator());
    SortedVector<AllMatches> maxmatches = new SortedVector<>(
        new AllMatches.MaxComparator());
    int[] dist = new int[10];

    print("Comparing: " + size + " submissions\n", null);

    long totalComparisons = (size * (size - 1)) / 2, count = 0, comparisons = 0;
    int index;
    AllMatches match;
    Submission s1, s2;
    long remain;
    String totalTimeStr, remainTime;

    print("Checking memory size...\n", null);

    // First try to load as many submissions as possible
    index = fillMemory(0, size);

    long startTime;
    long totalTime = 0;
    int startA = 0;
    int endA = index / 2;
    int startB = endA + 1;
    int endB = index;
    int i, j;
    // long progStart;

    do {
      // compare A to A
      startTime = System.currentTimeMillis();
      print("Comparing block A (" + startA + "-" + endA + ") to block A\n", null);

      for (i = startA; i <= endA; i++) {
        s1 = submissions.elementAt(i);

        if (s1.struct == null) {
          count += (endA - i);
          continue;
        }

        for (j = (i + 1); j <= endA; j++) {
          s2 = submissions.elementAt(j);

          if (s2.struct == null) {
            count++;
            continue;
          }

          match = this.gSTiling.compare(s1, s2);
          registerMatch(match, dist, avgmatches, maxmatches, null, i, j);
          comparisons++;
          count++;
        }
      }

      print("\n", null);
      totalTime += System.currentTimeMillis() - startTime;

      // Are we finished?
      if (startA == startB) {
        break;
      }

      do {
        totalTimeStr = "" + ((totalTime / 3600000 > 0) ? (totalTime / 3600000) + " h " : "")
            + ((totalTime / 60000 > 0) ? ((totalTime / 60000) % 60) + " min " : "") + (
            totalTime / 1000 % 60) + " sec";

        if (comparisons != 0) {
          remain = totalTime * (totalComparisons - count) / comparisons;
        } else {
          remain = 0;
        }

        remainTime = "" + ((remain / 3600000 > 0) ? (remain / 3600000) + " h " : "")
            + ((remain / 60000 > 0) ? ((remain / 60000) % 60) + " min " : "") + (remain / 1000 % 60)
            + " sec";

        print("Progress: " + (100 * count) / totalComparisons + "%\nTime used for comparisons: "
            + totalTimeStr
            + "\nRemaining time (estimate): " + remainTime + "\n", null);

        // compare A to B
        startTime = System.currentTimeMillis();
        print("Comparing block A (" + startA + "-" + endA + ") to block B (" + startB + "-" + endB
            + ")\n", null);

        for (i = startB; i <= endB; i++) {
          s1 = submissions.elementAt(i);

          if (s1.struct == null) {
            count += (endA - startA + 1);
            continue;
          }

          for (j = startA; j <= endA; j++) {
            s2 = submissions.elementAt(j);

            if (s2.struct == null) {
              count++;
              continue;
            }

            match = this.gSTiling.compare(s1, s2);
            registerMatch(match, dist, avgmatches, maxmatches, null, i, j);
            comparisons++;
            count++;
          }

          s1.struct = null; // remove B
        }

        print("\n", null);
        totalTime += System.currentTimeMillis() - startTime;

        if (endB == size - 1) {
          totalTimeStr = "" + ((totalTime / 3600000 > 0) ? (totalTime / 3600000) + " h " : "")
              + ((totalTime / 60000 > 0) ? ((totalTime / 60000) % 60) + " min " : "") + (
              totalTime / 1000 % 60) + " sec";
          remain = totalTime * (totalComparisons - count) / comparisons;
          remainTime = "" + ((remain / 3600000 > 0) ? (remain / 3600000) + " h " : "")
              + ((remain / 60000 > 0) ? ((remain / 60000) % 60) + " min " : "") + (remain / 1000
              % 60) + " sec";

          print("Progress: " + (100 * count) / totalComparisons + "%\nTime used for comparisons: "
              + totalTimeStr
              + "\nRemaining time (estimate): " + remainTime + "\n", null);
          break;
        }

        runtime.runFinalization();
        runtime.gc();
        Thread.yield();

        // Try to find the next B
        print("Finding next B\n", null);

        index = fillMemory(endB + 1, size);

        startB = endB + 1;
        endB = index;

      } while (true);

      // Remove A
      for (i = startA; i <= endA; i++) {
        submissions.elementAt(i).struct = null;
      }

      runtime.runFinalization();
      runtime.gc();
      Thread.yield();

      print("Find next A.\n", null);
      // First try to load as many submissions as possible

      index = fillMemory(endA + 1, size);

      if (index != size - 1) {
        startA = endA + 1;
        endA = startA + (index - startA + 1) / 2;
        startB = endA + 1;
        endB = index;
      } else {
        startA = startB; // last block
        endA = endB = index;
      }
    } while (true);

    totalTime += System.currentTimeMillis() - startTime;
    totalTimeStr = "" + ((totalTime / 3600000 > 0) ? (totalTime / 3600000) + " h " : "")
        + ((totalTime / 60000 > 0) ? ((totalTime / 60000) % 60000) + " min " : "") + (
        totalTime / 1000 % 60) + " sec";

    print("Total comparison time: " + totalTimeStr + "\nComparisons: " + count + "/" + comparisons
            + "/" + totalComparisons + "\n",
        null);

    // free remaining memory
    for (i = startA; i <= endA; i++) {
      submissions.elementAt(i).struct = null;
    }

    runtime.runFinalization();
    runtime.gc();
    Thread.yield();

    Cluster cluster = null;

    if (options.getClusterType() == ClusterType.NONE) {
      cluster = this.clusters.calculateClustering(submissions);
    }

    // TODO: Replace writeResults(...)
    // Deprecated:
    // writeResults(dist, avgmatches, maxmatches, null, cluster);
  }

  /*
   * Now the special comparison
   * TODO: Previously, this comparison created a `Report.java` (removed)
   * TODO: Check whether this comparison is now any different than the others after the report has been removed
   */
  private void specialCompare() {
    File root = new File(options.getResultDir());

    int size = submissions.size();
    int matchIndex = 0;

    print("Comparing: ", countValidSubmissions() + " submissions");
    print("\n(Writing results at the same time.)\n", null);

    int totalcomps = size * size;
    int i, j, anz = 0, count = 0;
    AllMatches match;
    Submission s1, s2;
    long msec = System.currentTimeMillis();

    for (i = 0; i < (size - 1); i++) {
      // Result vector
      SortedVector<AllMatches> matches = new SortedVector<>(
          new AllMatches.AvgComparator());

      s1 = submissions.elementAt(i);

      if (s1.struct == null) {
        count += (size - 1);
        continue;
      }

      for (j = 0; j < size; j++) {
        s2 = submissions.elementAt(j);

        if ((i == j) || (s2.struct == null)) {
          count++;
          continue;
        }

        match = this.gSTiling.compare(s1, s2);
        anz++;

        float percent = match.percent();

        if ((matches.size() < options.compare || matches.size() == 0 || match
            .moreThan(matches.lastElement().percent()))
            && match.moreThan(0)) {
          matches.insert(match);
          if (matches.size() > options.compare) {
            matches.removeElementAt(options.compare);
          }
        }

        if (options.getClusterType() != ClusterType.NONE) {
          options.similarity.setSimilarity(i, j, percent);
        }

        count++;
      }
    }

    long time = System.currentTimeMillis() - msec;
    print("\n", "Total time: " + ((time / 3600000 > 0) ? (time / 3600000) + " h " : "")
        + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
        + " sec\n" + "Time per comparison: "
        + (time / anz) + " msec\n");
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
        if (options.getSubDir() != null) {
          continue;
        }

        boolean hasValidSuffix = false;
        String name = subm_dir.getName();

        for (String suffix : options.getFileSuffixes()) {
          if (name.endsWith(suffix)) {
            hasValidSuffix = true;
            break;
          }
        }

        if (!hasValidSuffix) {
          continue;
        }

        submissions.addElement(new Submission(name, rootDir, this, options.getLanguageInstance()));

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
                options.getLanguageInstance()
            );
          } else {
            submissions.addElement(new Submission(
                subm_dir.getName(),
                file_dir,
                options.isRecursive(),
                this,
                options.getLanguageInstance()
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

  private int fillMemory(int from, int size) {
    Submission sub = null;
    int index = from;

    runtime.runFinalization();
    runtime.gc();
    Thread.yield();
    long freeBefore = runtime.freeMemory();
    try {
      for (; index < size; index++) {
        sub = submissions.elementAt(index);
        sub.struct = new Structure();
        if (!sub.struct.load(new File("temp", sub.dir.getName() + sub.name))) {
          sub.struct = null;
        }
      }
    } catch (java.lang.OutOfMemoryError e) {
      sub.struct = null;
      print("Memory overflow after loading " + (index - from + 1) + " submissions.\n", null);
    }
    if (index >= size) {
      index = size - 1;
    }

    if (freeBefore / runtime.freeMemory() <= 2) {
      return index;
    }
    for (int i = (index - from) / 2; i > 0; i--) {
      submissions.elementAt(index--).struct = null;
    }
    runtime.runFinalization();
    runtime.gc();
    Thread.yield();

    // make sure we freed half of the "available" memory.
    long free;
    while (freeBefore / (free = runtime.freeMemory()) > 2) {
      submissions.elementAt(index--).struct = null;
      runtime.runFinalization();
      runtime.gc();
      Thread.yield();
    }
    print(free / 1024 / 1024 + "MByte freed. Current index: " + index + "\n", null);

    return index;
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

  private void registerMatch(
      AllMatches match,
      int[] dist,
      SortedVector<AllMatches> avgMatches,
      SortedVector<AllMatches> maxMatches,
      SortedVector<AllMatches> minMatches,
      int a,
      int b) {
    float avgPercent = match.percent();
    float maxPercent = match.percentMaxAB();
    float minPercent = match.percentMinAB();

    dist[((((int) avgPercent) / 10) == 10) ? 9 : (((int) avgPercent) / 10)]++;

    if (!options.isStorePercent()) {
      if ((avgMatches.size() < options.getStoreMatches() || avgPercent > avgMatches.lastElement()
          .percent()) && avgPercent > 0) {
        avgMatches.insert(match);
        if (avgMatches.size() > options.getStoreMatches()) {
          avgMatches.removeElementAt(options.getStoreMatches());
        }
      }

      if (maxMatches != null && (maxMatches.size() < options.getStoreMatches()
          || maxPercent > maxMatches.lastElement().percent())
          && maxPercent > 0) {
        maxMatches.insert(match);
        if (maxMatches.size() > options.getStoreMatches()) {
          maxMatches.removeElementAt(options.getStoreMatches());
        }
      }

      if (minMatches != null && (minMatches.size() < options.getStoreMatches()
          || minPercent > minMatches.lastElement().percent())
          && minPercent > 0) {
        minMatches.insert(match);
        if (minMatches.size() > options.getStoreMatches()) {
          minMatches.removeElementAt(options.getStoreMatches());
        }
      }
    } else { // store_percent
      if (avgPercent > options.getStoreMatches()) {
        avgMatches.insert(match);
        if (avgMatches.size() > JPlagOptions.MAX_RESULT_PAIRS) {
          avgMatches.removeElementAt(JPlagOptions.MAX_RESULT_PAIRS);
        }
      }

      if (maxMatches != null && maxPercent > options.getStoreMatches()) {
        maxMatches.insert(match);
        if (maxMatches.size() > JPlagOptions.MAX_RESULT_PAIRS) {
          maxMatches.removeElementAt(JPlagOptions.MAX_RESULT_PAIRS);
        }
      }

      if (minMatches != null && minPercent > options.getStoreMatches()) {
        minMatches.insert(match);
        if (minMatches.size() > JPlagOptions.MAX_RESULT_PAIRS) {
          minMatches.removeElementAt(JPlagOptions.MAX_RESULT_PAIRS);
        }
      }
    }

    if (options.getClusterType() != ClusterType.NONE) {
      options.similarity.setSimilarity(a, b, avgPercent);
    }
  }
}
