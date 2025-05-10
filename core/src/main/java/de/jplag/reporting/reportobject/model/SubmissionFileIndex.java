package de.jplag.reporting.reportobject.model;

import java.util.Map;

public record SubmissionFileIndex(Map<String, Map<String, SubmissionFile>> fileIndexes) {
}
