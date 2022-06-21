package de.jplag.emf.model;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import de.jplag.emf.dynamic.DynamicMetamodelTokenConstants;
import de.jplag.testutils.TestErrorConsumer;

class ModelInstanceTest {
    private final Logger logger = LoggerFactory.getLogger("JPlag-Test");

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "books");

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
    void testParsingTestClass() {
        File baseFile = new File(BASE_PATH.toString());
        var testSubjects = Arrays.stream(baseFile.listFiles()).map(File::getName).filter(it -> !it.endsWith(Language.VIEW_FILE_SUFFIX))
                .toArray(String[]::new);

        TokenList result = frontend.parse(baseDirectory, testSubjects);
        assertNotEquals(0, result.size());
        List<String> treeViewFiles = Arrays.stream(testSubjects).map(it -> it + Language.VIEW_FILE_SUFFIX).collect(toList());
        System.out.println(TokenPrinter.printTokens(result, baseDirectory, treeViewFiles));

        logger.info("Dynamic token set: " + DynamicMetamodelTokenConstants.getTokenStrings());
        logger.info(result.allTokens().toString());
        assertEquals(6, result.size());
    }

    @AfterEach
    public void tearDown() {
        File baseFile = new File(BASE_PATH.toString());
        Arrays.stream(baseFile.listFiles()).filter(it -> it.getName().endsWith(Language.VIEW_FILE_SUFFIX)).forEach(File::delete);
    }

}
