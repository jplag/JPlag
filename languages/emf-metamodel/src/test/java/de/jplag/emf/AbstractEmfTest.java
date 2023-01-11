package de.jplag.emf;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import de.jplag.testutils.FileUtil;

/**
 * Shared test functionality for the tests of this language module.
 * @author Timur Saglam
 */
public abstract class AbstractEmfTest {

    protected static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "models");

    protected static final String[] TEST_SUBJECTS = {"bookStore.ecore", // base metamodel
            "bookStoreExtended.ecore", // extended version of base metamodel
            "bookStoreExtendedRefactor.ecore", // refactored version of the extended metamodel
            "bookStoreRenamed.ecore"}; // base metamodel with renamed elements

    protected de.jplag.Language language;
    protected File baseDirectory;

    @BeforeEach
    protected void setUp() {
        language = new Language();
        baseDirectory = BASE_PATH.toFile();
        FileUtil.assertDirectory(baseDirectory, TEST_SUBJECTS);
    }

    @AfterEach
    protected void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), Language.VIEW_FILE_SUFFIX);
    }
}
