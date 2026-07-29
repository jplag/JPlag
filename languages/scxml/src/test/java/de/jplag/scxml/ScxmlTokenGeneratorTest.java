package de.jplag.scxml;

import static de.jplag.SharedTokenType.FILE_END;
import static de.jplag.scxml.ScxmlTokenType.ACTION_END;
import static de.jplag.scxml.ScxmlTokenType.ASSIGNMENT;
import static de.jplag.scxml.ScxmlTokenType.CANCEL;
import static de.jplag.scxml.ScxmlTokenType.IF;
import static de.jplag.scxml.ScxmlTokenType.IF_END;
import static de.jplag.scxml.ScxmlTokenType.ON_ENTRY;
import static de.jplag.scxml.ScxmlTokenType.ON_EXIT;
import static de.jplag.scxml.ScxmlTokenType.SEND;
import static de.jplag.scxml.ScxmlTokenType.STATE;
import static de.jplag.scxml.ScxmlTokenType.STATE_END;
import static de.jplag.scxml.ScxmlTokenType.TRANSITION;
import static de.jplag.scxml.ScxmlTokenType.TRANSITION_END;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.scxml.parser.ScxmlParserAdapter;
import de.jplag.scxml.parser.SimpleScxmlTokenGenerator;
import de.jplag.scxml.sorting.NoOpSortingStrategy;
import de.jplag.scxml.sorting.RecursiveSortingStrategy;
import de.jplag.scxml.util.AbstractScxmlVisitor;
import de.jplag.testutils.FileUtil;

class ScxmlTokenGeneratorTest {

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "statecharts");

    enum TestSubjects {
        COMPLEX("complex.scxml"),
        COMPLEX_REORDERED("complex_reordered.scxml"),
        // Cannot use .scxmlview extension as that would delete it in tearDown
        COMPLEX_VIEW_FILE("complex_expected_scxmlview"),
        COVERAGE("coverage.scxml");

        private final String fileName;

        TestSubjects(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    private final File baseDirectory = BASE_PATH.toFile();

    private List<TokenType> getTokenTypes(ScxmlParserAdapter adapter, File testFile) throws ParsingException {
        return adapter.parse(Set.of(testFile)).stream().map(Token::getType).toList();
    }

    @Test
    void testRecursiveSorter() throws ParsingException {
        File originalTestFile = new File(baseDirectory, TestSubjects.COMPLEX.fileName);
        ConfigurableScxmlParserAdapter adapter = new ConfigurableScxmlParserAdapter();
        AbstractScxmlVisitor visitor = new SimpleScxmlTokenGenerator(adapter);
        adapter.configure(visitor, new NoOpSortingStrategy());

        List<TokenType> expectedTokenTypes = List.of(STATE, STATE, TRANSITION, ASSIGNMENT, TRANSITION_END, STATE_END, STATE, ON_ENTRY, ASSIGNMENT,
                ACTION_END, TRANSITION, TRANSITION_END, STATE, ON_ENTRY, IF, ASSIGNMENT, IF_END, ACTION_END, TRANSITION, TRANSITION_END, STATE_END,
                STATE, ON_ENTRY, SEND, ACTION_END, ON_EXIT, CANCEL, ACTION_END, TRANSITION, TRANSITION_END, TRANSITION, TRANSITION_END, STATE_END,
                STATE_END, STATE_END, FILE_END);

        List<TokenType> originalTokenTypes = getTokenTypes(adapter, originalTestFile);
        assertEquals(expectedTokenTypes, originalTokenTypes);
        adapter.setSorter(new RecursiveSortingStrategy(visitor));

        File reorderedTestFile = new File(baseDirectory, TestSubjects.COMPLEX_REORDERED.fileName);
        List<TokenType> reorderedTokenTypes = getTokenTypes(adapter, reorderedTestFile);
        // Check that the token sequences is the same when applying the recursive sorting strategy on the reordered file
        assertEquals(expectedTokenTypes, reorderedTokenTypes);
    }

    @Test
    void testCoverage() throws ParsingException {
        File testFile = new File(baseDirectory, TestSubjects.COVERAGE.fileName);
        ScxmlParserAdapter adapter = new ScxmlParserAdapter();
        List<TokenType> actualUniqueTokenTypes = getTokenTypes(adapter, testFile).stream().filter(x -> x != FILE_END).distinct().toList();

        assertThat(actualUniqueTokenTypes).containsExactlyInAnyOrder(ScxmlTokenType.values());
    }

    @Test
    void testViewFile() throws ParsingException, IOException {
        File testFile = new File(baseDirectory, TestSubjects.COMPLEX.fileName);
        ScxmlParserAdapter adapter = new ScxmlParserAdapter();
        adapter.parse(Set.of(testFile));

        File viewFile = new File(testFile.getPath() + ScxmlLanguage.VIEW_FILE_EXTENSION);
        File expectedViewFile = new File(baseDirectory, TestSubjects.COMPLEX_VIEW_FILE.fileName);
        assertTrue(viewFile.exists());
        assertEquals(Files.readAllLines(expectedViewFile.toPath()), Files.readAllLines(viewFile.toPath()));
    }

    @AfterEach
    void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), ScxmlLanguage.VIEW_FILE_EXTENSION);
    }
}
