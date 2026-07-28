package de.jplag;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;

import de.jplag.inputs.SubmissionFile;
import org.junit.jupiter.api.Test;

class ParsingExceptionTest {

    @Test
    void testParsingExceptionAcceptsNullReason() {
        // placeholder exception to have a non-null argument
        SubmissionFile file = new DummySubmissionFile(new File("myFile"), "myFile");
        // placeholder exception to have a non-null argument
        Exception exception = new RuntimeException();

        assertDoesNotThrow(() -> new ParsingException(file));
        assertDoesNotThrow(() -> new ParsingException(file, (String) null));
        assertDoesNotThrow(() -> new ParsingException(file, exception));
        assertDoesNotThrow(() -> new ParsingException(file, null, exception));
    }
}
