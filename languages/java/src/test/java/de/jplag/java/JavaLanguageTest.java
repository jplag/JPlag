package de.jplag.java;

import static de.jplag.java.JavaTokenType.J_APPLY;
import static de.jplag.java.JavaTokenType.J_ARRAY_INIT_BEGIN;
import static de.jplag.java.JavaTokenType.J_ARRAY_INIT_END;
import static de.jplag.java.JavaTokenType.J_ASSIGN;
import static de.jplag.java.JavaTokenType.J_CATCH_BEGIN;
import static de.jplag.java.JavaTokenType.J_CATCH_END;
import static de.jplag.java.JavaTokenType.J_CLASS_BEGIN;
import static de.jplag.java.JavaTokenType.J_CLASS_END;
import static de.jplag.java.JavaTokenType.J_COND;
import static de.jplag.java.JavaTokenType.J_FINALLY_BEGIN;
import static de.jplag.java.JavaTokenType.J_FINALLY_END;
import static de.jplag.java.JavaTokenType.J_IF_BEGIN;
import static de.jplag.java.JavaTokenType.J_IF_END;
import static de.jplag.java.JavaTokenType.J_IMPORT;
import static de.jplag.java.JavaTokenType.J_LOOP_BEGIN;
import static de.jplag.java.JavaTokenType.J_LOOP_END;
import static de.jplag.java.JavaTokenType.J_METHOD_BEGIN;
import static de.jplag.java.JavaTokenType.J_METHOD_END;
import static de.jplag.java.JavaTokenType.J_NEWARRAY;
import static de.jplag.java.JavaTokenType.J_NEWCLASS;
import static de.jplag.java.JavaTokenType.J_PACKAGE;
import static de.jplag.java.JavaTokenType.J_RECORD_BEGIN;
import static de.jplag.java.JavaTokenType.J_RECORD_END;
import static de.jplag.java.JavaTokenType.J_RETURN;
import static de.jplag.java.JavaTokenType.J_THROW;
import static de.jplag.java.JavaTokenType.J_TRY_BEGIN;
import static de.jplag.java.JavaTokenType.J_TRY_END;
import static de.jplag.java.JavaTokenType.J_VARDEF;

import java.util.List;

import de.jplag.TokenType;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Unit tests for the Java language module.
 */
public class JavaLanguageTest extends LanguageModuleTest {

    /**
     * Creates an instance of the language test.
     */
    public JavaLanguageTest() {
        super(new JavaLanguage(), JavaTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        // Test cases regarding the extraction from if and else conditions.
        collector.testFile("IfElse.java", "IfIf.java", "IfElseIf.java").testSourceCoverage().testTokenSequence(J_IMPORT, J_CLASS_BEGIN,
                J_METHOD_BEGIN, J_VARDEF, J_IF_BEGIN, J_THROW, J_NEWCLASS, J_IF_END, J_IF_BEGIN, J_APPLY, J_APPLY, J_IF_END, J_METHOD_END,
                J_CLASS_END);

        // Test cases regarding the extraction from implicit vs. explicit blocks in Java code.
        collector.testFile("IfWithBraces.java", "IfWithoutBraces.java").testSourceCoverage().testTokenSequence(J_PACKAGE, J_IMPORT, J_CLASS_BEGIN,
                J_METHOD_BEGIN, J_VARDEF, J_IF_BEGIN, J_THROW, J_NEWCLASS, J_IF_END, J_IF_BEGIN, J_APPLY, J_APPLY, J_IF_END, J_IF_BEGIN, J_APPLY,
                J_IF_END, J_METHOD_END, J_CLASS_END);

        collector.testFile("Verbose.java", "Compact.java").testSourceCoverage().testTokenSequence(J_PACKAGE, J_IMPORT, J_CLASS_BEGIN, J_METHOD_BEGIN,
                J_VARDEF, J_VARDEF, J_IF_BEGIN, J_APPLY, J_RETURN, J_IF_END, J_VARDEF, J_LOOP_BEGIN, J_VARDEF, J_APPLY, J_ASSIGN, J_IF_BEGIN, J_APPLY,
                J_APPLY, J_ASSIGN, J_IF_END, J_LOOP_END, J_IF_BEGIN, J_APPLY, J_ASSIGN, J_IF_END, J_IF_BEGIN, J_APPLY, J_APPLY, J_ASSIGN, J_IF_END,
                J_RETURN, J_METHOD_END, J_CLASS_END);

        // Test difference between try block and try-with-resource block.
        collector.testFile("Try.java", "TryWithResource.java").testSourceCoverage().testTokenSequence(J_PACKAGE, J_IMPORT, J_IMPORT, J_IMPORT,
                J_CLASS_BEGIN, J_METHOD_BEGIN, J_VARDEF, J_APPLY, J_NEWCLASS, J_METHOD_END, J_METHOD_BEGIN, J_VARDEF, J_VARDEF, J_TRY_BEGIN, J_VARDEF,
                J_ASSIGN, J_NEWCLASS, J_NEWCLASS, J_LOOP_BEGIN, J_APPLY, J_APPLY, J_APPLY, J_LOOP_END, J_CATCH_BEGIN, J_VARDEF, J_APPLY, J_CATCH_END,
                J_FINALLY_BEGIN, J_IF_BEGIN, J_APPLY, J_IF_END, J_FINALLY_END, J_TRY_END, J_METHOD_END, J_CLASS_END);

        collector.testFile("CLI.java").testSourceCoverage().testContainedTokens(J_TRY_END, J_IMPORT, J_VARDEF, J_LOOP_BEGIN, J_ARRAY_INIT_BEGIN,
                J_IF_BEGIN, J_CATCH_END, J_COND, J_ARRAY_INIT_END, J_METHOD_BEGIN, J_TRY_BEGIN, J_CLASS_END, J_RETURN, J_ASSIGN, J_METHOD_END,
                J_IF_END, J_CLASS_BEGIN, J_NEWARRAY, J_PACKAGE, J_APPLY, J_LOOP_END, J_THROW, J_NEWCLASS, J_CATCH_BEGIN);

        collector.testFile("PatternMatching.java", "PatternMatchingManual.java").testSourceCoverage().testTokenSequence(J_CLASS_BEGIN, J_RECORD_BEGIN,
                J_VARDEF, J_RECORD_END, J_METHOD_BEGIN, J_VARDEF, J_NEWCLASS, J_IF_BEGIN, J_VARDEF, J_IF_END, J_METHOD_END, J_CLASS_END);

        collector.testFile("StringConcat.java").testSourceCoverage().testTokenSequence(J_CLASS_BEGIN, J_METHOD_BEGIN, J_VARDEF, J_VARDEF, J_VARDEF,
                J_APPLY, J_METHOD_END, J_CLASS_END);

        collector.testFile("AnonymousVariables.java").testTokenSequence(J_CLASS_BEGIN, J_METHOD_BEGIN, J_VARDEF, J_IF_BEGIN, J_IF_END, J_METHOD_END,
                J_CLASS_END);

        collector.testFile("ClassWithoutModifier.java").testSourceCoverage().testTokenSequence(J_PACKAGE, J_IMPORT, J_IMPORT, J_IMPORT, J_CLASS_BEGIN,
                J_CLASS_END, J_CLASS_BEGIN, J_CLASS_END);

        collector.testFile("NormalSourceFile.java", "CompactSourceFile.java").testSourceCoverage().testTokenSequence(J_CLASS_BEGIN, J_METHOD_BEGIN,
                J_APPLY, J_METHOD_END, J_METHOD_BEGIN, J_APPLY, J_METHOD_END, J_CLASS_END);

        collector.addTokenPositionTests("tokenPositions");
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreLinesByPrefix("//");
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByPrefix("})");
        collector.ignoreByCondition(line -> line.contains("else") && !line.contains("if"));
    }

    @Override
    protected List<TokenType> getIgnoredTokensForMonotoneTokenOrder() {
        return List.of(JavaTokenType.J_ANNO);
    }
}
