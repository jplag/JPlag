package de.jplag.emf.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.emf.AbstractEmfTest;
import de.jplag.emf.Language;

class EmfaticModelViewTest extends AbstractEmfTest {

    private static final String EXPECTED_VIEWS = "emfatic";

    @Test
    @DisplayName("Test content of emfatic view files of example metamodels")
    void testEmfaticViewFiles() {

        for (String modelName : TEST_SUBJECTS) {
            // Load models:
            File modelFile = new File(baseDirectory, modelName);
            assertTrue(modelFile.exists());
            Resource modelResource = EMFUtil.loadModelResource(modelFile);
            assertNotNull(modelResource);

            // Generate emfatic view:
            EmfaticModelView view = new EmfaticModelView(modelFile, modelResource);
            view.writeToFile(Language.VIEW_FILE_SUFFIX);

            // Compare expected vs. actual view file:
            File viewFile = new File(modelFile.getPath() + Language.VIEW_FILE_SUFFIX);
            assertTrue(viewFile.exists());
            File expectedViewFile = BASE_PATH.resolveSibling(Path.of(EXPECTED_VIEWS, viewFile.getName())).toFile();
            assertTrue(expectedViewFile.exists());
            try {
                assertEquals(Files.readString(expectedViewFile.toPath()), Files.readString(viewFile.toPath()));
            } catch (IOException exception) {
                fail(exception);
            }
        }

    }

}
