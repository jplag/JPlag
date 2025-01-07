package de.jplag.csharp;

import static de.jplag.csharp.CSharpTokenAttribute.ACCESSORS_BEGIN;
import static de.jplag.csharp.CSharpTokenAttribute.ACCESSORS_END;
import static de.jplag.csharp.CSharpTokenAttribute.ACCESSOR_BEGIN;
import static de.jplag.csharp.CSharpTokenAttribute.ACCESSOR_END;
import static de.jplag.csharp.CSharpTokenAttribute.ASSIGNMENT;
import static de.jplag.csharp.CSharpTokenAttribute.CLASS;
import static de.jplag.csharp.CSharpTokenAttribute.CLASS_BEGIN;
import static de.jplag.csharp.CSharpTokenAttribute.CLASS_END;
import static de.jplag.csharp.CSharpTokenAttribute.CONSTRUCTOR;
import static de.jplag.csharp.CSharpTokenAttribute.FIELD;
import static de.jplag.csharp.CSharpTokenAttribute.IF;
import static de.jplag.csharp.CSharpTokenAttribute.IF_BEGIN;
import static de.jplag.csharp.CSharpTokenAttribute.IF_END;
import static de.jplag.csharp.CSharpTokenAttribute.INVOCATION;
import static de.jplag.csharp.CSharpTokenAttribute.LOCAL_VARIABLE;
import static de.jplag.csharp.CSharpTokenAttribute.METHOD;
import static de.jplag.csharp.CSharpTokenAttribute.METHOD_BEGIN;
import static de.jplag.csharp.CSharpTokenAttribute.METHOD_END;
import static de.jplag.csharp.CSharpTokenAttribute.PROPERTY;
import static de.jplag.csharp.CSharpTokenAttribute.RETURN;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class CSharpTest extends LanguageModuleTest {
    public CSharpTest() {
        super(new CSharpLanguage(), CSharpTokenAttribute.class);
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
