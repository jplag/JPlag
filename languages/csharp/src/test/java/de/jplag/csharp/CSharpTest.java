package de.jplag.csharp;

import static de.jplag.csharp.CSharpTokenType.ACCESSORS_BEGIN;
import static de.jplag.csharp.CSharpTokenType.ACCESSORS_END;
import static de.jplag.csharp.CSharpTokenType.ACCESSOR_BEGIN;
import static de.jplag.csharp.CSharpTokenType.ACCESSOR_END;
import static de.jplag.csharp.CSharpTokenType.ASSIGNMENT;
import static de.jplag.csharp.CSharpTokenType.CLASS;
import static de.jplag.csharp.CSharpTokenType.CLASS_BEGIN;
import static de.jplag.csharp.CSharpTokenType.CLASS_END;
import static de.jplag.csharp.CSharpTokenType.CONSTRUCTOR;
import static de.jplag.csharp.CSharpTokenType.FIELD;
import static de.jplag.csharp.CSharpTokenType.IF;
import static de.jplag.csharp.CSharpTokenType.IF_BEGIN;
import static de.jplag.csharp.CSharpTokenType.IF_END;
import static de.jplag.csharp.CSharpTokenType.INVOCATION;
import static de.jplag.csharp.CSharpTokenType.LOCAL_VARIABLE;
import static de.jplag.csharp.CSharpTokenType.METHOD;
import static de.jplag.csharp.CSharpTokenType.METHOD_BEGIN;
import static de.jplag.csharp.CSharpTokenType.METHOD_END;
import static de.jplag.csharp.CSharpTokenType.PROPERTY;
import static de.jplag.csharp.CSharpTokenType.RETURN;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Unit test for the C# language module, verifying tokenization and source coverage. Extends {@link LanguageModuleTest}
 * to provide C#-specific test files, token sequences, and rules for ignoring irrelevant source lines such as comments,
 * preprocessor directives, and using-alias statements.
 */
public class CSharpTest extends LanguageModuleTest {

    /**
     * Constructs a C# language test module with the appropriate language and token type.
     */
    public CSharpTest() {
        super(new CSharpLanguage(), CSharpTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("TestClass.cs").testSourceCoverage().testTokenSequence(CLASS, CLASS_BEGIN, FIELD, CONSTRUCTOR, METHOD_BEGIN,
                LOCAL_VARIABLE, METHOD_END, METHOD, METHOD_BEGIN, IF, IF_BEGIN, INVOCATION, IF_END, IF, IF_BEGIN, INVOCATION, IF_END, METHOD_END,
                PROPERTY, ACCESSORS_BEGIN, ACCESSOR_BEGIN, ACCESSOR_END, ACCESSOR_BEGIN, ACCESSOR_END, ACCESSORS_END, FIELD, PROPERTY,
                ACCESSORS_BEGIN, ACCESSOR_BEGIN, RETURN, ACCESSOR_END, ACCESSOR_BEGIN, ASSIGNMENT, ACCESSOR_END, ACCESSORS_END, CLASS_END);

        collector.testFile("AllInOneNoPreprocessor.cs").testSourceCoverage().testTokenCoverage();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByPrefix("//");
        collector.ignoreLinesByRegex(".*//test-ignore");

        // Using (import) as alias
        collector.ignoreLinesByRegex("using.*=.*<.*>.*;");
        collector.ignoreLinesByRegex("using.*=[^.]+;");

        collector.ignoreLinesByPrefix("extern");
        collector.ignoreByCondition(line -> line.trim().matches("[a-zA-Z0-9]+:.*"));
    }
}
