package de.jplag.reporting.reportobject.mapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.jplag.JPlagResult;
import de.jplag.Submission;

/**
 * Responsible for creating a mapping of all Submissions to their respective report viewer ids. This mapping is achieved
 * by associating the {@link Submission#getSimpleName()} (JPlag's internal Submission uid) to a report viewer id. Currently,
 * the sanitized version of the {@link Submission#getSimpleName()} serves as report viewer id.
 */
public class SubmissionNameToIdMapper {

    private SubmissionNameToIdMapper() {
        // private constructor for non-instantiability.
    }

    private static final String FILE_SEPARATOR_REPLACEMENT = "_";
}
