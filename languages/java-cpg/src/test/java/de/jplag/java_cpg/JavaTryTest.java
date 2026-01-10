package de.jplag.java_cpg;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.TokenEquivalenceModel;
import de.jplag.java_cpg.token.cpg.CpgTokenEquivalenceModel;

/**
 * Test cases regarding the extraction edge try vs. try with resource.
 */
class JavaTryTest extends AbstractJavaCpgLanguageTest {
    @Test
    @DisplayName("Test difference between try block and try-with-resource block.")
    void testJavaClassPair() throws ParsingException {
        TokenEquivalenceModel model = new CpgTokenEquivalenceModel();

        assertIterableEquals(parseJavaFile("try/Try.java", true, false).stream().map(model::getPrimaryType).toList(),
                parseJavaFile("try/TryWithResource.java", true, false).stream().map(model::getPrimaryType).toList());
    }
}
