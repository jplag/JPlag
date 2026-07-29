package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.commentextraction.Comment;
import de.jplag.commentextraction.CommentExtractor;
import de.jplag.commentextraction.CommentExtractorSettings;
import de.jplag.commentextraction.CommentType;
import de.jplag.commentextraction.EnvironmentDelimiter;

/**
 * Tests the comment extraction on arbitrary files.
 */
class CommentExtractorTest {

    private static final Path TEST_FILE_LOCATION = Path.of("src", "test", "resources", "de", "jplag", "samples");
    private static final String TEST_FILE_NAME = "CommentExtractorTest.txt";

    @Test
    @DisplayName("Tests the comment extractor on a text file with line and multiline comments")
    void testCommentExtractor() {
        CommentExtractorSettings settings = new CommentExtractorSettings(List.of(new EnvironmentDelimiter("\"")), // No comment environments
                List.of("//"), // Line comments
                List.of(new EnvironmentDelimiter("/*", "*/")), // Block comments
                List.of("\\") // Escape characters
        );

        File input = new File(TEST_FILE_LOCATION.toFile(), TEST_FILE_NAME);

        CommentExtractor extractor = new CommentExtractor(input, settings);

        List<Comment> comments = extractor.extract();

        assertEquals(2, comments.size());
        assertEquals(new Comment(input, " This is a line comment.", 2, 3, CommentType.LINE), comments.get(0));

        String multilineComment = String.join(System.lineSeparator(), " This is", "a", "multiline", "comment", "");

        assertEquals(new Comment(input, multilineComment, 4, 3, CommentType.BLOCK), comments.get(1));
    }

}
