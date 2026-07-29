package de.jplag.emf.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.ParsingException;
import de.jplag.emf.AbstractEmfTest;
import de.jplag.emf.EmfLanguage;

class EmfaticModelViewTest extends AbstractEmfTest {

    private static final String EXPECTED_VIEW_FOLDER = "emfatic";

    private static List<String> provideModelNames() {
        return Arrays.asList(TEST_SUBJECTS);
    }

    @ParameterizedTest
    @DisplayName("Test content of emfatic view files of example metamodels")
    @MethodSource("provideModelNames")
    void testEmfaticViewFiles(String modelName) throws ParsingException {
        // Load model:
        File modelFile = new File(baseDirectory, modelName);
        Resource modelResource = loadAndVerifyModel(modelFile);

        // Generate emfatic view:
        EmfaticModelView view = new EmfaticModelView(modelFile, modelResource);
        view.writeToFile(EmfLanguage.VIEW_FILE_EXTENSION);

        // Compare expected vs. actual view file:
        assertViewFilesMatch(modelFile, EmfLanguage.VIEW_FILE_EXTENSION, EXPECTED_VIEW_FOLDER);
    }
}
