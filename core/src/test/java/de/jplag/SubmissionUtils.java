package de.jplag;

import de.jplag.inputs.SubmissionFolder;
import de.jplag.inputs.SubmissionInputData;

public class SubmissionUtils {
    private SubmissionUtils() {
        //empty constructor for checkstyle
    }

    public static Submission cloneWithNewSubmissionName(Submission submission, String name, Language lang) {
        SubmissionInputData inputData = submission.getInputData();
        SubmissionFolder newFolder = inputData.getFolder().asRootWithNewName(name);
        SubmissionInputData newInputData = new SubmissionInputData(inputData.getSource(), newFolder, inputData.isNew(), inputData.isMultiRoot());
        return new Submission(newInputData, lang);
    }
}
