package de.jplag.scxml;

import static de.jplag.SharedTokenType.FILE_END;
import static de.jplag.scxml.ScxmlTokenType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.scxml.parser.ScxmlParserAdapter;
import de.jplag.scxml.parser.SimpleScxmlTokenGenerator;
import de.jplag.scxml.sorting.NoOpSortingStrategy;
import de.jplag.scxml.sorting.RecursiveSortingStrategy;
import de.jplag.scxml.util.AbstractScxmlVisitor;

public class ScxmlTokenGeneratorTest {

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "statecharts");
    private static final String[] TEST_SUBJECTS = {"complex.scxml", "reordered.scxml", "coverage.scxml"};
    private final File baseDirectory = BASE_PATH.toFile();

    private List<TokenType> getTokenTypes(ScxmlParserAdapter adapter, File testFile) throws ParsingException {
        return adapter.parse(Set.of(testFile)).stream().map(Token::getType).toList();
    }

    @Test
    void testRecursiveSorter() throws ParsingException {
        File originalTestFile = new File(baseDirectory, TEST_SUBJECTS[0]);
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

        File reorderedTestFile = new File(baseDirectory, TEST_SUBJECTS[1]);
        List<TokenType> reorderedTokenTypes = getTokenTypes(adapter, reorderedTestFile);
        // Check that the token sequences is the same when applying the recursive sorter on the reordered file
        assertEquals(expectedTokenTypes, reorderedTokenTypes);
    }

    @Test
    void testCoverage() throws ParsingException {
        File testFile = new File(baseDirectory, TEST_SUBJECTS[2]);
        ScxmlParserAdapter adapter = new ScxmlParserAdapter();
        List<TokenType> actualUniqueTokenTypes = getTokenTypes(adapter, testFile).stream().filter(x -> x != FILE_END).distinct().toList();

        assertThat(actualUniqueTokenTypes).containsExactlyInAnyOrder(ScxmlTokenType.values());
    }
}
