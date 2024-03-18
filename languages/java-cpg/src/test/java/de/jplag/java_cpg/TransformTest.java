package de.jplag.java_cpg;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;

/**
 * Test cases regarding the extraction edge try vs. try with resource.
 */
class TransformTest extends AbstractJavaCpgLanguageTest {
    @Test
    @DisplayName("Test the transformation of source code files to graphs.")
    void testJavaTransformation() throws ParsingException {
        parseJavaFile("GetterSetter.java", true);
    }
}
