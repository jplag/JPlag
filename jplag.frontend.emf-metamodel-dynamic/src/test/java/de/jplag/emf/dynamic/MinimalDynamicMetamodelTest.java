package de.jplag.emf.dynamic;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenList;
import de.jplag.TokenPrinter;
import de.jplag.testutils.TestErrorConsumer;
import de.jplag.testutils.TokenUtils;

class MinimalDynamicMetamodelTest {
    private final Logger logger = LoggerFactory.getLogger("JPlag-Test");

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "models");
    private static final String[] TEST_SUBJECT = {"bookstore.ecore", "bookstoreExtended.ecore", "bookstoreRenamed.ecore"};

    private de.jplag.Language frontend;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        TestErrorConsumer consumer = new TestErrorConsumer();
        frontend = new Language(consumer);
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    @Test
    void testBookstoreMetamodels() {
        TokenList result = frontend.parse(baseDirectory, TEST_SUBJECT);
        List<String> treeViewFiles = Arrays.stream(TEST_SUBJECT).map(it -> it + Language.VIEW_FILE_SUFFIX).collect(toList());

        logger.debug(TokenPrinter.printTokens(result, baseDirectory, treeViewFiles));
        logger.info(("Dynamic token set: " + DynamicMetamodelTokenConstants.getTokenStrings()));
        logger.info("parsed tokens: " + result.allTokens().toString());
        assertEquals(7, DynamicMetamodelTokenConstants.getTokenStrings().size());
        assertEquals(64, result.size());

        var bookstoreTokens = TokenUtils.tokenTypesByFile(result, TEST_SUBJECT[0]);
        var bookstoreRenamedTokens = TokenUtils.tokenTypesByFile(result, TEST_SUBJECT[2]);
        var bookstoreExtendedTokens = TokenUtils.tokenTypesByFile(result, TEST_SUBJECT[1]);
        assertTrue(bookstoreTokens.size() < bookstoreExtendedTokens.size());
        assertIterableEquals(bookstoreTokens, bookstoreRenamedTokens);
    }

    @AfterEach
    public void tearDown() {
        File baseFile = new File(BASE_PATH.toString());
        Arrays.stream(baseFile.listFiles()).filter(it -> it.getName().endsWith(Language.VIEW_FILE_SUFFIX)).forEach(File::delete);
    }
}
