package de.jplag.emf.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.emf.AbstractEmfTest;
import de.jplag.emf.Language;

class EmfaticModelViewTest extends AbstractEmfTest {

    private static final String EXPECTED_VIEW_FOLDER = "emfatic";

    private static List<String> provideModelNames() {
        return Arrays.asList(TEST_SUBJECTS);
    }

    @ParameterizedTest
    @DisplayName("Test content of emfatic view files of example metamodels")
    @MethodSource("provideModelNames")
    void testEmfaticViewFiles(String modelName) {
        // Load model:
        File modelFile = new File(baseDirectory, modelName);
        assertTrue(modelFile.exists());
        Resource modelResource = EMFUtil.loadModelResource(modelFile);
        assertNotNull(modelResource);

        // Generate emfatic view:
        EmfaticModelView view = new EmfaticModelView(modelFile, modelResource);
        view.writeToFile(Language.VIEW_FILE_SUFFIX);

        // Compare expected vs. actual view file:
        File viewFile = new File(modelFile.getPath() + Language.VIEW_FILE_SUFFIX);
        File expectedViewFile = BASE_PATH.resolveSibling(Path.of(EXPECTED_VIEW_FOLDER, viewFile.getName())).toFile();
        assertTrue(viewFile.exists());
        assertTrue(expectedViewFile.exists());
        try {
            assertEquals(Files.readAllLines(expectedViewFile.toPath()), Files.readAllLines(viewFile.toPath()));
        } catch (IOException exception) {
            fail(exception);
        }
    }
}
