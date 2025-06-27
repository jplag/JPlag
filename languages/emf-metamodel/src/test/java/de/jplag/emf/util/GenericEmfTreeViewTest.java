package de.jplag.emf.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.emf.AbstractEmfTest;
import de.jplag.testutils.FileUtil;

class GenericEmfTreeViewTest extends AbstractEmfTest {

    private static final String VIEW_FILE_EXTENSION = ".treeview";
    private static final String EXPECTED_VIEW_FOLDER = "treeview";

    private static List<String> provideModelNames() {
        return Arrays.asList(TEST_SUBJECTS);
    }

    @ParameterizedTest
    @DisplayName("Test content of generic EMF view files of example metamodels")
    @MethodSource("provideModelNames")
    void testEmfaticViewFiles(String modelName) {
        // Load model:
        File modelFile = new File(baseDirectory, modelName);
        Resource modelResource = loadAndVerifyModel(modelFile);

        // Generate emfatic view:
        GenericEmfTreeView view = new GenericEmfTreeView(modelFile, modelResource);
        view.writeToFile(VIEW_FILE_EXTENSION);

        // Compare expected vs. actual view file:
        assertViewFilesMatch(modelFile, VIEW_FILE_EXTENSION, EXPECTED_VIEW_FOLDER);
    }

    @AfterEach
    @Override
    protected void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), VIEW_FILE_EXTENSION);
    }

}
