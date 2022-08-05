package de.jplag.reporting.reportobject.mapper;

import de.jplag.Submission;
import de.jplag.reporting.reportobject.model.SubmissionName;


public class SubmissionNameMapper {
    public static SubmissionName submissionNameOf(Submission submission){
        return new SubmissionName(submission.getNameSanitized(), submission.getName());
    }
}
