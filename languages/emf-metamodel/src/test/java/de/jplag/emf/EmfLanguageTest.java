package de.jplag.emf;

import static de.jplag.emf.MetamodelTokenAttribute.ATTRIBUTE;
import static de.jplag.emf.MetamodelTokenAttribute.CLASS;
import static de.jplag.emf.MetamodelTokenAttribute.CLASS_END;
import static de.jplag.emf.MetamodelTokenAttribute.CONTAINMENT_MULT;
import static de.jplag.emf.MetamodelTokenAttribute.PACKAGE;
import static de.jplag.emf.MetamodelTokenAttribute.PACKAGE_END;

import org.junit.jupiter.api.AfterEach;

import de.jplag.testutils.FileUtil;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Basic EMF test that mainly serves the purpose of checking ascending line indices for the tokens with Emfatic views.
 */
public class EmfLanguageTest extends LanguageModuleTest {

    public EmfLanguageTest() {
        super(new EmfLanguage(), MetamodelTokenAttribute.class);
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
        FileUtil.clearFiles(getTestFileLocation(), EmfLanguage.VIEW_FILE_SUFFIX); // clean up the view files.
    }

}
