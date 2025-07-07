package de.jplag.emf;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import de.jplag.emf.util.EMFUtil;
import de.jplag.testutils.FileUtil;

/**
 * Shared test functionality for the tests of this language module.
 * @author Timur Saglam
 */
public abstract class AbstractEmfTest {

    protected static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "emf");

    protected static final String[] TEST_SUBJECTS = {"bookStore.ecore", // base metamodel
            "bookStoreExtended.ecore", // extended version of base metamodel
            "bookStoreExtendedRefactor.ecore", // refactored version of the extended metamodel
            "bookStoreRenamed.ecore"}; // base metamodel with renamed elements

    protected de.jplag.Language language;
    protected File baseDirectory;

    @BeforeEach
    protected void setUp() {
        language = new EmfLanguage();
        baseDirectory = BASE_PATH.toFile();
        FileUtil.assertDirectory(baseDirectory, TEST_SUBJECTS);
    }

    @AfterEach
    protected void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), EmfLanguage.VIEW_FILE_EXTENSION);
    }

    /**
     * Load (meta)model from file and assert it is correctly loaded.
     * @param modelFile is the file to load.
     * @return the loaded resource.
     */
    protected Resource loadAndVerifyModel(File modelFile) {
        assertTrue(modelFile.exists());
        Resource modelResource = EMFUtil.loadModelResource(modelFile);
        assertNotNull(modelResource);
        return modelResource;
    }

    /**
     * Compares the generated view file of a meta(model) with an expected one.
     * @param modelFile is the file of the meta(model).
     * @param viewFileSuffix is the suffix of the view file.
     * @param directoryOfExpectedViews is the name of the folder where the expected view files are located.
     */
    protected void assertViewFilesMatch(File modelFile, String viewFileSuffix, String directoryOfExpectedViews) {
        File viewFile = new File(modelFile.getPath() + viewFileSuffix);
        File expectedViewFile = BASE_PATH.resolveSibling(Path.of(directoryOfExpectedViews, viewFile.getName())).toFile();
        assertTrue(viewFile.exists());
        assertTrue(expectedViewFile.exists());
        try {
            assertLinesMatch(Files.readAllLines(expectedViewFile.toPath()), Files.readAllLines(viewFile.toPath()));
        } catch (IOException exception) {
            fail(exception);
        }
    }
}
