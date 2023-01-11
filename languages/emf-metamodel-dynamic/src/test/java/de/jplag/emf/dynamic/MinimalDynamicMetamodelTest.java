package de.jplag.emf.dynamic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;
import de.jplag.testutils.FileUtil;
import de.jplag.testutils.TokenUtils;

class MinimalDynamicMetamodelTest {
    private final Logger logger = LoggerFactory.getLogger(MinimalDynamicMetamodelTest.class);

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "models");
    private static final String[] TEST_SUBJECTS = {"bookStore.ecore", "bookStoreExtended.ecore", "bookStoreExtendedRefactor.ecore",
            "bookStoreRenamed.ecore"};

    private de.jplag.Language language;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        language = new Language();
        baseDirectory = BASE_PATH.toFile();
        FileUtil.assertDirectory(baseDirectory, TEST_SUBJECTS);
    }

    @Test
    void testBookstoreMetamodels() throws ParsingException {
        List<File> testFiles = Arrays.stream(TEST_SUBJECTS).map(path -> new File(BASE_PATH.toFile(), path)).toList();
        List<Token> result = language.parse(new HashSet<>(testFiles));
        List<TokenType> tokenTypes = result.stream().map(Token::getType).toList();
        logger.debug(TokenPrinter.printTokens(result, baseDirectory, Optional.of(Language.VIEW_FILE_SUFFIX)));
        logger.info("parsed token types: " + tokenTypes.stream().map(TokenType::getDescription).toList().toString());
        assertEquals(94, tokenTypes.size());
        assertEquals(7, new HashSet<>(tokenTypes.stream().filter(DynamicMetamodelTokenType.class::isInstance).toList()).size());

        var originalTokens = TokenUtils.tokenTypesByFile(result, testFiles.get(0));
        var renamedTokens = TokenUtils.tokenTypesByFile(result, testFiles.get(3));
        var extendedTokens = TokenUtils.tokenTypesByFile(result, testFiles.get(1));
        var renamedRefactorTokens = TokenUtils.tokenTypesByFile(result, testFiles.get(2));
        assertTrue(originalTokens.size() < extendedTokens.size());
        assertTrue(renamedTokens.size() < renamedRefactorTokens.size());
        assertIterableEquals(originalTokens, renamedTokens);
    }

    @AfterEach
    public void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), Language.VIEW_FILE_SUFFIX);
    }
}
