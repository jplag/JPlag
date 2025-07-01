package de.jplag.emf.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.emf.EmfLanguage;
import de.jplag.testutils.FileUtil;

class MinimalModelInstanceTest {
    private final Logger logger = LoggerFactory.getLogger(MinimalModelInstanceTest.class);

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "books");
    private static final String[] TEST_SUBJECTS = {"bookStore.ecore", "bookStore.xml", "bookStore2.xml"};

    private EmfModelLanguage language;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        language = new EmfModelLanguage();
        baseDirectory = BASE_PATH.toFile();
        FileUtil.assertDirectory(baseDirectory, TEST_SUBJECTS);
    }

    @Test
    @DisplayName("Test tokens extracted from generated example instances")
    void testBookStoreInstances() {
        File baseFile = new File(BASE_PATH.toString());
        List<File> baseFiles = new ArrayList<>(Arrays.asList(baseFile.listFiles()));
        var sortedFiles = new LinkedHashSet<>(language.customizeSubmissionOrder(baseFiles));
        try {
            List<Token> tokens = language.parse(sortedFiles, true);
            assertNotEquals(0, tokens.size());
            logger.debug(TokenPrinter.printTokens(tokens, baseDirectory, Optional.of(EmfLanguage.VIEW_FILE_EXTENSION)));
            logger.info("Parsed tokens: " + tokens);
            assertEquals(7, tokens.size());
        } catch (ParsingException e) {
            fail("Parsing failed: " + e.getMessage(), e);
        }

    }

    @AfterEach
    public void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), EmfLanguage.VIEW_FILE_EXTENSION);
    }

}
