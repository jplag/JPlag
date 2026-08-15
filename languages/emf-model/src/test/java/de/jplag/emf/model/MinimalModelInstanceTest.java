package de.jplag.emf.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import de.jplag.util.PathUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenPrinterUtils;
import de.jplag.testutils.FileUtil;

class MinimalModelInstanceTest {
    private final Logger logger = LoggerFactory.getLogger(MinimalModelInstanceTest.class);

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "books");
    private static final Path METAMODEL_PATH = BASE_PATH.resolveSibling("bookStore.ecore");
    private static final String[] TEST_SUBJECTS = {"bookStore.xml", "bookStore2.xml"};

    private EmfModelLanguage language;
    private EmfLanguageOptions options;

    @BeforeEach
    public void setUp() throws IOException {
        language = new EmfModelLanguage();
        options = (EmfLanguageOptions) language.getOptions();
        FileUtil.assertDirectory(BASE_PATH, TEST_SUBJECTS);
    }

    @Test
    @DisplayName("Test tokens extracted from generated example instances")
    void testBookStoreInstances() throws IOException {
        List<Path> baseFiles = Files.list(BASE_PATH).toList();
        options.getMetamodelPathOption().setValue(METAMODEL_PATH.toString());
        try {
            List<Token> tokens = language.parse(new HashSet<>(baseFiles), true);
            assertNotEquals(0, tokens.size());
            logger.debug(TokenPrinterUtils.printTokensByFile(tokens, file -> PathUtils.appendSuffix(file, EmfModelLanguage.VIEW_FILE_EXTENSION)));
            logger.info("Parsed tokens: " + tokens);
            assertEquals(7, tokens.size());
        } catch (ParsingException e) {
            fail("Parsing failed: " + e.getMessage(), e);
        }

    }

    @AfterEach
    public void tearDown() {
        FileUtil.clearFiles(BASE_PATH, EmfModelLanguage.VIEW_FILE_EXTENSION);
    }

}
