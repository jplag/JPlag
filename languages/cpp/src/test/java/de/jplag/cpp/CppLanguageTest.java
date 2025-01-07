package de.jplag.cpp;

import java.util.Arrays;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * These tests attempt to cover the cpp module in multiple ways. These are the tests currently contained:
 * <p>
 * - As the ANTLR grammar requires some workarounds to have the token extraction similar to the Java language module,
 * there are tests to cover the extraction of such tokens. - Ensures that all tokens are extracted at some point and
 * source files are suitably covered by testing a huge file (bc6h_enc.h)
 */
public class CppLanguageTest extends LanguageModuleTest {
    private static final String[] assignSnippets = {"i = 10", "i += 10", "i -= 10", "i += 10", "i /= 10", "i %= 10", "i >>= 10", "i <<= 10",
            "i &= 10", "i ^= 10", "i |= 10", "i++", "i--", "++i", "--i"};

    private static final String[] functionCallSnippets = {"this->myMethod(v)", "MyClass::myMethod(v)", "myMethod(v)", "m.myMethod(v)", "myMethod(1)",
            "myMethod(\"a\")"};

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
        super(new CPPLanguage(), CPPTokenAttribute.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.inlineSource(formattedCode(intMethodCallFormatter, assignSnippets)).testContainedTokens(CPPTokenAttribute.ASSIGN)
                .testSourceCoverage();

        collector.testFile("FunctionCall.cpp").testTokenSequence(CPPTokenAttribute.FUNCTION_BEGIN, CPPTokenAttribute.APPLY, CPPTokenAttribute.APPLY,
                CPPTokenAttribute.APPLY, CPPTokenAttribute.APPLY, CPPTokenAttribute.FUNCTION_END).testSourceCoverage();

        collector.testFile("Loop.cpp")
                .testTokenSequence(CPPTokenAttribute.FUNCTION_BEGIN, CPPTokenAttribute.DO_BEGIN, CPPTokenAttribute.GOTO, CPPTokenAttribute.DO_END,
                        CPPTokenAttribute.WHILE_BEGIN, CPPTokenAttribute.BREAK, CPPTokenAttribute.WHILE_END, CPPTokenAttribute.FOR_BEGIN,
                        CPPTokenAttribute.CONTINUE, CPPTokenAttribute.FOR_END, CPPTokenAttribute.RETURN, CPPTokenAttribute.FUNCTION_END)
                .testSourceCoverage();

        collector.inlineSource(formattedCode(stringMethodCallFormatter, functionCallSnippets)).testTokenSequence(CPPTokenAttribute.FUNCTION_BEGIN,
                CPPTokenAttribute.VARDEF, CPPTokenAttribute.APPLY, CPPTokenAttribute.FUNCTION_END).testSourceCoverage();

        collector.testFile("IfElse.cpp")
                .testTokenSequence(CPPTokenAttribute.FUNCTION_BEGIN, CPPTokenAttribute.VARDEF, CPPTokenAttribute.VARDEF, CPPTokenAttribute.VARDEF,
                        CPPTokenAttribute.VARDEF, CPPTokenAttribute.IF_BEGIN, CPPTokenAttribute.ASSIGN, CPPTokenAttribute.ELSE,
                        CPPTokenAttribute.IF_BEGIN, CPPTokenAttribute.ASSIGN, CPPTokenAttribute.ASSIGN, CPPTokenAttribute.ELSE,
                        CPPTokenAttribute.ASSIGN, CPPTokenAttribute.IF_END, CPPTokenAttribute.IF_END, CPPTokenAttribute.FUNCTION_END)
                .testSourceCoverage();

        collector.inlineSource("double* b = new double[10];").testTokenSequence(CPPTokenAttribute.VARDEF, CPPTokenAttribute.ASSIGN,
                CPPTokenAttribute.NEWARRAY);

        collector.inlineSource("int x = square(2);").testTokenSequence(CPPTokenAttribute.VARDEF, CPPTokenAttribute.ASSIGN, CPPTokenAttribute.APPLY);

        collector.testFile("CallOutsideMethodInClass.cpp").testTokenSequence(CPPTokenAttribute.CLASS_BEGIN, CPPTokenAttribute.VARDEF,
                CPPTokenAttribute.ASSIGN, CPPTokenAttribute.APPLY, CPPTokenAttribute.CLASS_END);

        collector.testFile("Union.cpp").testTokenSequence(CPPTokenAttribute.UNION_BEGIN, CPPTokenAttribute.VARDEF, CPPTokenAttribute.VARDEF,
                CPPTokenAttribute.VARDEF, CPPTokenAttribute.UNION_END).testSourceCoverage();

        collector.testFile("IntArray.cpp").testTokenSequence(CPPTokenAttribute.VARDEF, CPPTokenAttribute.ASSIGN, CPPTokenAttribute.BRACED_INIT_BEGIN,
                CPPTokenAttribute.BRACED_INIT_END, CPPTokenAttribute.VARDEF, CPPTokenAttribute.BRACED_INIT_BEGIN, CPPTokenAttribute.BRACED_INIT_END);

        collector.testFile("bc6h_enc.h").testCoverages();

        collector.inlineSource("""
                void test() {
                if(true) {
                    if(false) {
                    } else {
                    }
                } else {
                }
                }
                """);
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreByCondition(line -> line.matches(" *[a-zA-Z]+: *"));
        collector.ignoreByCondition(line -> line.matches(" *\\{*"));
        collector.ignoreLinesByPrefix("}");
        collector.ignoreLinesByPrefix("//");
        collector.ignoreLinesByPrefix("#");
        collector.ignoreLinesByPrefix("namespace");
        collector.ignoreLinesByPrefix("using");
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByContains("else");
    }

    private String[] formattedCode(String formatter, String... snippets) {
        return Arrays.stream(snippets).map(formatter::formatted).toArray(String[]::new);
    }
}
