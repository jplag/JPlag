package de.jplag.inputs;

import java.io.File;
import java.util.List;

public interface SubmissionDirectory {
    List<SubmissionFolder> resolveSubmissions();

    String name();

    /**
     * Compatibility method to check if a file is immediately contained in the subdirectory
     *
     * @param file The file to check
     * @return True if the file is directly contained in the submission directory
     */
    @Deprecated
    boolean contains(File file);
}
