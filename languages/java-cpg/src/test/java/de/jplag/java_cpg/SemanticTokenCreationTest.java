package de.jplag.java_cpg;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SemanticTokenCreationTest extends AbstractJavaCpgLanguageTest {

    @Test
    @DisplayName("Test semantic token creation for if.")
    void testSemanticTokenCreation() throws Exception {
        parseJavaFile("longSubmission/GameBoard.java", false);
    }

    @Test
    @DisplayName("Test semantic token creation for if.")
    void testSemanticTokenCreationComplex() throws Exception {
        parseJavaFile("longSubmission/Board.java", false);
    }

}
