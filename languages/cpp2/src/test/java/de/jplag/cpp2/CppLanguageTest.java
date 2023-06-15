package de.jplag.cpp2;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

import java.util.Arrays;

public class CppLanguageTest extends LanguageModuleTest {
    private static final String[] assignSnippets = {"i = 10", "i += 10", "i -= 10", "i += 10", "i /= 10", "i %= 10",
            "i >>= 10", "i <<= 10", "i &= 10", "i ^= 10", "i |= 10", "i++", "i--", "++i", "--i"};

    private static final String[] functionCallSnippets = {"this->myMethod(v)", "MyClass::myMethod(v)", "myMethod(v)",
            "m.myMethod(v)", "myMethod(1)", "myMethod(\"a\")"};

    private static final String intMethodCallFormatter = """
            void f(int i) {
                %s;
            }
            """;

    private static final String stringMethodCallFormatter = """
            void a(string v) {
              %s;
            }
            """;

    public CppLanguageTest() {
        super(new CPPLanguage(), CPPTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.inlineSource(formattedCode(intMethodCallFormatter, assignSnippets))
                .testContainedTokens(CPPTokenType.ASSIGN).testSourceCoverage();

        collector.testFile("FunctionCall.cpp").testTokenSequence(CPPTokenType.FUNCTION_BEGIN,
                CPPTokenType.APPLY, CPPTokenType.APPLY, CPPTokenType.APPLY, CPPTokenType.APPLY,
                CPPTokenType.FUNCTION_END).testSourceCoverage();

        collector.testFile("Loop.cpp").testTokenSequence(CPPTokenType.FUNCTION_BEGIN, CPPTokenType.DO_BEGIN,
                CPPTokenType.GOTO, CPPTokenType.DO_END, CPPTokenType.WHILE_BEGIN, CPPTokenType.BREAK,
                CPPTokenType.WHILE_END, CPPTokenType.FOR_BEGIN, CPPTokenType.CONTINUE, CPPTokenType.FOR_END,
                CPPTokenType.RETURN, CPPTokenType.FUNCTION_END).testSourceCoverage();

        collector.inlineSource(formattedCode(stringMethodCallFormatter, functionCallSnippets))
                .testTokenSequence(CPPTokenType.FUNCTION_BEGIN, CPPTokenType.VARDEF, CPPTokenType.APPLY,
                        CPPTokenType.FUNCTION_END).testSourceCoverage();

        collector.testFile("IfElse.cpp").testTokenSequence(CPPTokenType.FUNCTION_BEGIN,
                        CPPTokenType.VARDEF, CPPTokenType.VARDEF, CPPTokenType.VARDEF, CPPTokenType.VARDEF,
                        CPPTokenType.IF_BEGIN, CPPTokenType.ASSIGN, CPPTokenType.ELSE, CPPTokenType.IF_BEGIN,
                        CPPTokenType.ASSIGN, CPPTokenType.ASSIGN, CPPTokenType.ELSE, CPPTokenType.ASSIGN,
                        CPPTokenType.IF_END, CPPTokenType.IF_END, CPPTokenType.FUNCTION_END)
                .testSourceCoverage();

        collector.inlineSource("double* b = new double[10];").testTokenSequence(
                CPPTokenType.VARDEF, CPPTokenType.ASSIGN, CPPTokenType.NEWARRAY);

        collector.inlineSource("int x = square(2);").testTokenSequence(CPPTokenType.VARDEF,
                CPPTokenType.ASSIGN, CPPTokenType.APPLY);

        collector.testFile("CallOutsideMethodInClass.cpp").testTokenSequence(CPPTokenType.CLASS_BEGIN,
                CPPTokenType.VARDEF, CPPTokenType.ASSIGN, CPPTokenType.APPLY, CPPTokenType.CLASS_END);

        collector.testFile("Union.cpp").testTokenSequence(CPPTokenType.UNION_BEGIN, CPPTokenType.VARDEF,
                CPPTokenType.VARDEF, CPPTokenType.VARDEF, CPPTokenType.UNION_END).testSourceCoverage();

        collector.testFile("IntArray.cpp").testTokenSequence(CPPTokenType.VARDEF, CPPTokenType.ASSIGN,
                CPPTokenType.BRACED_INIT_BEGIN, CPPTokenType.BRACED_INIT_END, CPPTokenType.VARDEF,
                CPPTokenType.BRACED_INIT_BEGIN, CPPTokenType.BRACED_INIT_END);

        collector.testFile("bc6h_enc.h").testCoverages();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreByCondition(line -> line.matches(" *[a-zA-Z]+: *"));
        collector.ignoreByCondition(line -> line.matches(" *\\{*"));
        collector.ignoreByCondition(line -> line.matches(" *}*"));
    }

    private String[] formattedCode(String formatter, String... snippets) {
        return Arrays.stream(snippets).map(formatter::formatted).toArray(String[]::new);
    }
}
