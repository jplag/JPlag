package de.jplag.emf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;
import de.jplag.testutils.TokenUtils;

class MinimalMetamodelTest extends AbstractEmfTest {
    private final Logger logger = LoggerFactory.getLogger(MinimalMetamodelTest.class);

    @Test
    @DisplayName("Test tokens generated from example metamodels")
    void testBookstoreMetamodels() throws ParsingException {
        List<File> testFiles = Arrays.stream(TEST_SUBJECTS).map(path -> new File(BASE_PATH.toFile(), path)).toList();
        List<Token> result = language.parse(new HashSet<>(testFiles));

        logger.debug(TokenPrinter.printTokens(result, baseDirectory, Optional.of(Language.VIEW_FILE_SUFFIX)));
        List<TokenType> tokenTypes = result.stream().map(Token::getType).toList();
        logger.info("Parsed token types: " + tokenTypes.stream().map(TokenType::getDescription).toList().toString());
        assertEquals(82, tokenTypes.size());
        assertEquals(13, new HashSet<>(tokenTypes).size());

        var originalTokens = TokenUtils.tokenTypesByFile(result, testFiles.get(0));
        var renamedTokens = TokenUtils.tokenTypesByFile(result, testFiles.get(3));
        var extendedTokens = TokenUtils.tokenTypesByFile(result, testFiles.get(1));
        var renamedRefactorTokens = TokenUtils.tokenTypesByFile(result, testFiles.get(2));
        assertTrue(originalTokens.size() < extendedTokens.size());
        assertTrue(renamedTokens.size() < renamedRefactorTokens.size());
        assertIterableEquals(originalTokens, renamedTokens);
    }

}
