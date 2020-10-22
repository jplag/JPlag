package jplag.strategy;

import java.util.Vector;
import jplag.AllMatches;
import jplag.GSTiling;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.Submission;

public class ExperimentalComparisonStrategy extends AbstractComparisonStrategy {

  public ExperimentalComparisonStrategy(JPlagOptions options, GSTiling gSTiling) {
    super(options, gSTiling);
  }

  // EXPERIMENT !!!!! special compare routine!
  @Override
  public JPlagResult compareSubmissions(
      Vector<Submission> submissions,
      Submission baseCodeSubmission
  ) {
    int size = submissions.size();
    int[] similarity = new int[(size * size - size) / 2];

    int anzSub = submissions.size();
    int i, j, count = 0;
    Submission s1, s2;
    AllMatches match;
    long msec = System.currentTimeMillis();

    for (i = 0; i < (anzSub - 1); i++) {
      s1 = submissions.elementAt(i);
      if (s1.tokenList == null) {
        continue;
      }
      for (j = (i + 1); j < anzSub; j++) {
        s2 = submissions.elementAt(j);
        if (s2.tokenList == null) {
          continue;
        }

        match = this.gSTiling.compare(s1, s2);
        similarity[count++] = (int) match.percent();
      }
    }

    long time = System.currentTimeMillis() - msec;

    // output
    System.out.print(options.getRootDirName() + " ");
    System.out.print(options.getMinTokenMatch() + " ");
    System.out.print(options.getFilter() + " ");
    System.out.print((time) + " ");

    for (i = 0; i < similarity.length; i++) {
      System.out.print(similarity[i] + " ");
    }

    System.out.println();

    // TODO
    return new JPlagResult();
  }
}
