package de.jplag.inputs;

import java.util.Comparator;
import java.util.Objects;

public class SubmissionIdentifier implements Comparable<SubmissionIdentifier> {
    private static final String DELIMITER = "/";

    private String submissionDirectoryName;
    private String submissionName;

    public SubmissionIdentifier(String humanReadableInput) {
        if(humanReadableInput.contains(DELIMITER)) {
            String[] parts = humanReadableInput.split(DELIMITER);
            this.submissionDirectoryName = parts[0];
            this.submissionName = parts[1];
        } else {
            this.submissionDirectoryName = null;
            this.submissionName = humanReadableInput;
        }
    }

    public SubmissionIdentifier(String submissionDirectoryName, String submissionName) {
        this.submissionDirectoryName = submissionDirectoryName;
        this.submissionName = submissionName;
    }

    public String getSubmissionDirectoryName() {
        return submissionDirectoryName;
    }

    public String getSubmissionName() {
        return submissionName;
    }

    public String getFullyQualified() {
        return submissionDirectoryName + DELIMITER + submissionName;
    }

    public String getName(boolean isMultiRootRun) {
        if (isMultiRootRun) {
            return getFullyQualified();
        } else {
            return getSubmissionName();
        }
    }

    @Override
    public int compareTo(SubmissionIdentifier other) {
        return Comparator.comparing(SubmissionIdentifier::getSubmissionDirectoryName)
                .thenComparing(SubmissionIdentifier::getSubmissionName)
                .compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionIdentifier that = (SubmissionIdentifier) o;
        return Objects.equals(submissionDirectoryName, that.submissionDirectoryName) && Objects.equals(submissionName, that.submissionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submissionDirectoryName, submissionName);
    }
}
