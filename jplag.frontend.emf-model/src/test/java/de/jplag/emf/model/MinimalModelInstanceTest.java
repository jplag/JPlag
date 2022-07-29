package de.jplag.emf.model;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenList;
import de.jplag.TokenPrinter;
import de.jplag.emf.dynamic.DynamicMetamodelTokenConstants;
import de.jplag.testutils.TestErrorConsumer;

class MinimalModelInstanceTest {
    private final Logger logger = LoggerFactory.getLogger("JPlag-Test");

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "books");

    private Language frontend;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        TestErrorConsumer consumer = new TestErrorConsumer();
        frontend = new Language(consumer);
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    @Test
    void testBookStoreInstances() {
        File baseFile = new File(BASE_PATH.toString());
        var sortedFiles = frontend.customizeSubmissionOrder(new ArrayList<>(Arrays.stream(baseFile.listFiles()).toList()));
        var testSubjects = sortedFiles.stream().map(File::getName).filter(it -> !it.endsWith(Language.VIEW_FILE_SUFFIX)).toArray(String[]::new);

        TokenList result = frontend.parse(baseDirectory, testSubjects);
        assertNotEquals(0, result.size());

        List<String> treeViewFiles = Arrays.stream(testSubjects).map(it -> it + Language.VIEW_FILE_SUFFIX).collect(toList());
        logger.info(TokenPrinter.printTokens(result, baseDirectory, treeViewFiles));
        logger.info("Dynamic token set: " + DynamicMetamodelTokenConstants.getTokenStrings());
        logger.info("Parsed tokens: " + result.allTokens().toString());

        assertEquals(7, result.size());
    }

    @AfterEach
    public void tearDown() {
        File baseFile = new File(BASE_PATH.toString());
        Arrays.stream(baseFile.listFiles()).filter(it -> it.getName().endsWith(Language.VIEW_FILE_SUFFIX)).forEach(File::delete);
    }

}
