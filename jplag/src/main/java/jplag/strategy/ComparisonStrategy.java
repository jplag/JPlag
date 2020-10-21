package jplag.strategy;

import java.util.Vector;
import jplag.JPlagResult;
import jplag.Submission;

public interface ComparisonStrategy {

  JPlagResult compareSubmissions(Vector<Submission> submissions, Submission baseCodeSubmission);

}
