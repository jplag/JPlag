package de.jplag.inputs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface SubmissionFile extends SubmissionDirectoryComponent {
    InputStream open() throws IOException;

    /**
     *
     * @return
     */
    URI asUri() throws IOException;

    SubmissionFile clone(String newPath);
}
