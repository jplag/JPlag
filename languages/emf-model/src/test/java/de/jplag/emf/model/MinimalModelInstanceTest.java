package de.jplag.emf.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
    private File baseDirectory;
    private EmfLanguageOptions options;

    @BeforeEach
    public void setUp() {
        language = new EmfModelLanguage();
        options = (EmfLanguageOptions) language.getOptions();
        baseDirectory = BASE_PATH.toFile();
        FileUtil.assertDirectory(baseDirectory, TEST_SUBJECTS);
    }

    @Test
    @DisplayName("Test tokens extracted from generated example instances")
    void testBookStoreInstances() {
        File baseFile = new File(BASE_PATH.toString());
        List<File> baseFiles = new ArrayList<>(Arrays.asList(baseFile.listFiles()));
        options.getMetamodelPathOption().setValue(METAMODEL_PATH.toString());
        try {
            List<Token> tokens = language.parse(new HashSet<>(baseFiles), true);
            assertNotEquals(0, tokens.size());
            logger.debug(
                    TokenPrinterUtils.printTokensByFile(tokens, file -> new File(file.getAbsolutePath() + EmfModelLanguage.VIEW_FILE_EXTENSION)));
            logger.info("Parsed tokens: " + tokens);
            assertEquals(7, tokens.size());
        } catch (ParsingException e) {
            fail("Parsing failed: " + e.getMessage(), e);
        }

    }

    @AfterEach
    public void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), EmfModelLanguage.VIEW_FILE_EXTENSION);
    }

}
