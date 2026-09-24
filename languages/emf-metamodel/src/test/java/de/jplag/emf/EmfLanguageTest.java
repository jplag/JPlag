package de.jplag.emf;

import static de.jplag.emf.MetamodelTokenType.ATTRIBUTE;
import static de.jplag.emf.MetamodelTokenType.CLASS;
import static de.jplag.emf.MetamodelTokenType.CLASS_END;
import static de.jplag.emf.MetamodelTokenType.CONTAINMENT_MULT;
import static de.jplag.emf.MetamodelTokenType.PACKAGE;
import static de.jplag.emf.MetamodelTokenType.PACKAGE_END;

import org.junit.jupiter.api.AfterEach;

import de.jplag.testutils.FileUtil;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Basic EMF test that mainly serves the purpose of checking ascending line indices for the tokens with Emfatic views.
 */
public class EmfLanguageTest extends LanguageModuleTest {

    /**
     * Creates the test suite.
     */
    public EmfLanguageTest() {
        super(new EmfLanguage(), MetamodelTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testAllOfType(EmfLanguage.FILE_ENDING).testContainedTokens(PACKAGE, PACKAGE_END, CLASS, CLASS_END, ATTRIBUTE, CONTAINMENT_MULT);

    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        // None, does not really apply for modeling artifacts.
    }

    @AfterEach
    protected void tearDown() {
        FileUtil.clearFiles(getTestFileLocation(), EmfLanguage.VIEW_FILE_EXTENSION); // clean up the view files.
    }

}
