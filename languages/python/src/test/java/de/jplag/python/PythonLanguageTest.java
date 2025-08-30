package de.jplag.python;

import static de.jplag.python.PythonTokenType.APPLY;
import static de.jplag.python.PythonTokenType.ASSIGN;
import static de.jplag.python.PythonTokenType.ASYNC;
import static de.jplag.python.PythonTokenType.AWAIT;
import static de.jplag.python.PythonTokenType.BREAK;
import static de.jplag.python.PythonTokenType.CASE;
import static de.jplag.python.PythonTokenType.CLASS_BEGIN;
import static de.jplag.python.PythonTokenType.CLASS_END;
import static de.jplag.python.PythonTokenType.CONTINUE;
import static de.jplag.python.PythonTokenType.DICTIONARY;
import static de.jplag.python.PythonTokenType.DICT_COMPREHENSION;
import static de.jplag.python.PythonTokenType.EXCEPT_BEGIN;
import static de.jplag.python.PythonTokenType.EXCEPT_END;
import static de.jplag.python.PythonTokenType.EXCEPT_GROUP_BEGIN;
import static de.jplag.python.PythonTokenType.EXCEPT_GROUP_END;
import static de.jplag.python.PythonTokenType.FINALLY_BEGIN;
import static de.jplag.python.PythonTokenType.FINALLY_END;
import static de.jplag.python.PythonTokenType.FOR_BEGIN;
import static de.jplag.python.PythonTokenType.FOR_END;
import static de.jplag.python.PythonTokenType.IF_BEGIN;
import static de.jplag.python.PythonTokenType.IF_END;
import static de.jplag.python.PythonTokenType.IMPORT;
import static de.jplag.python.PythonTokenType.LAMBDA;
import static de.jplag.python.PythonTokenType.LIST;
import static de.jplag.python.PythonTokenType.LIST_COMPREHENSION;
import static de.jplag.python.PythonTokenType.MATCH_BEGIN;
import static de.jplag.python.PythonTokenType.MATCH_END;
import static de.jplag.python.PythonTokenType.METHOD_BEGIN;
import static de.jplag.python.PythonTokenType.METHOD_END;
import static de.jplag.python.PythonTokenType.NAMED_EXPR;
import static de.jplag.python.PythonTokenType.PASS;
import static de.jplag.python.PythonTokenType.RAISE;
import static de.jplag.python.PythonTokenType.RETURN;
import static de.jplag.python.PythonTokenType.SET_COMPREHENSION;
import static de.jplag.python.PythonTokenType.TRY_BEGIN;
import static de.jplag.python.PythonTokenType.TRY_END;
import static de.jplag.python.PythonTokenType.TYPE_ALIAS;
import static de.jplag.python.PythonTokenType.WHILE_BEGIN;
import static de.jplag.python.PythonTokenType.WHILE_END;
import static de.jplag.python.PythonTokenType.WITH_BEGIN;
import static de.jplag.python.PythonTokenType.WITH_END;
import static de.jplag.python.PythonTokenType.YIELD;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Test class for the Tree-sitter based Python language module.
 */
public class PythonLanguageTest extends LanguageModuleTest {
    public PythonLanguageTest() {
        super(new PythonLanguage(), PythonTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("BasicTest.py").testSourceCoverage().testTokenSequence(IMPORT, IMPORT, CLASS_BEGIN, METHOD_BEGIN, ASSIGN, ASSIGN, LIST,
                METHOD_END, METHOD_BEGIN, ASSIGN, APPLY, RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, APPLY, RETURN, METHOD_END, METHOD_BEGIN, IF_BEGIN,
                RETURN, IF_END, RETURN, APPLY, APPLY, METHOD_END, CLASS_END, METHOD_BEGIN, IF_BEGIN, RETURN, DICTIONARY, IF_END, ASSIGN, ASSIGN,
                FOR_BEGIN, ASSIGN, IF_BEGIN, ASSIGN, IF_END, FOR_END, RETURN, DICTIONARY, APPLY, METHOD_END, METHOD_BEGIN, IF_BEGIN, RETURN, LIST,
                IF_BEGIN, RETURN, LIST, IF_END, IF_END, ASSIGN, LIST, WHILE_BEGIN, APPLY, APPLY, WHILE_END, RETURN, METHOD_END, METHOD_BEGIN,
                TRY_BEGIN, APPLY, RETURN, EXCEPT_BEGIN, RETURN, EXCEPT_END, TRY_END, METHOD_END, METHOD_BEGIN, ASSIGN, APPLY, ASSIGN, APPLY, ASSIGN,
                APPLY, ASSIGN, LIST, ASSIGN, APPLY, IF_BEGIN, APPLY, APPLY, IF_END, FOR_BEGIN, APPLY, IF_BEGIN, CONTINUE, IF_END, IF_BEGIN, BREAK,
                IF_END, APPLY, FOR_END, IF_BEGIN, NAMED_EXPR, APPLY, APPLY, IF_END, ASSIGN, LIST, IF_BEGIN, NAMED_EXPR, APPLY, APPLY, IF_BEGIN, APPLY,
                IF_END, IF_END, TRY_BEGIN, ASSIGN, APPLY, APPLY, EXCEPT_BEGIN, APPLY, EXCEPT_END, TRY_END, ASSIGN, LAMBDA, LAMBDA, ASSIGN,
                LIST_COMPREHENSION, APPLY, RETURN, APPLY, METHOD_END, IF_BEGIN, ASSIGN, APPLY, APPLY, IF_END);

        collector.testFile("AsyncTest.py").testSourceCoverage().testTokenSequence(METHOD_BEGIN, ASYNC, AWAIT, AWAIT, APPLY, YIELD, YIELD, METHOD_END,
                METHOD_BEGIN, ASYNC, FOR_BEGIN, ASYNC, APPLY, AWAIT, AWAIT, APPLY, FOR_END, METHOD_END, METHOD_BEGIN, ASYNC, WITH_BEGIN, ASYNC, APPLY,
                AWAIT, AWAIT, APPLY, WITH_END, METHOD_END);

        collector.testFile("ExceptionTest.py").testSourceCoverage().testTokenSequence(TRY_BEGIN, APPLY, EXCEPT_BEGIN, PASS, EXCEPT_END, FINALLY_BEGIN,
                PASS, FINALLY_END, TRY_END);

        collector.testFile("MatchTest.py").testSourceCoverage().testTokenSequence(METHOD_BEGIN, MATCH_BEGIN, CASE, RETURN, CASE, RETURN, CASE, RETURN,
                MATCH_END, METHOD_END);

        collector.testFile("ExpressionsTest.py").testSourceCoverage().testTokenSequence(METHOD_BEGIN, ASSIGN, LAMBDA, LAMBDA, ASSIGN, LAMBDA, LAMBDA,
                ASSIGN, LAMBDA, LAMBDA, ASSIGN, LIST, ASSIGN, APPLY, APPLY, LAMBDA, LAMBDA, ASSIGN, APPLY, APPLY, LAMBDA, LAMBDA, RETURN, METHOD_END,
                METHOD_BEGIN, ASSIGN, LIST, IF_BEGIN, NAMED_EXPR, APPLY, APPLY, IF_END, WHILE_BEGIN, NAMED_EXPR, APPLY, APPLY, IF_BEGIN, APPLY, BREAK,
                IF_END, WHILE_END, ASSIGN, LIST_COMPREHENSION, APPLY, NAMED_EXPR, IF_BEGIN, NAMED_EXPR, APPLY, RETURN, IF_END, METHOD_END,
                METHOD_BEGIN, ASSIGN, LAMBDA, LAMBDA, LIST_COMPREHENSION, IF_BEGIN, NAMED_EXPR, APPLY, LIST, ASSIGN, LAMBDA, LAMBDA, APPLY, RETURN,
                APPLY, IF_END, METHOD_END, IF_BEGIN, APPLY, APPLY, APPLY, IF_END);

        collector.testFile("ControlFlowTest.py").testSourceCoverage().testTokenSequence(METHOD_BEGIN, ASSIGN, WHILE_BEGIN, ASSIGN, IF_BEGIN, BREAK,
                IF_END, WHILE_END, ASSIGN, LIST, ASSIGN, WHILE_BEGIN, ASSIGN, IF_BEGIN, CONTINUE, IF_END, APPLY, IF_BEGIN, APPLY, BREAK, IF_END,
                WHILE_END, RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, LIST, FOR_BEGIN, APPLY, ASSIGN, LIST, ASSIGN, WHILE_BEGIN, ASSIGN, IF_BEGIN,
                CONTINUE, IF_END, IF_BEGIN, BREAK, IF_END, APPLY, WHILE_END, APPLY, FOR_END, RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, LIST,
                FOR_BEGIN, APPLY, IF_BEGIN, CONTINUE, IF_END, ASSIGN, WHILE_BEGIN, ASSIGN, IF_BEGIN, CONTINUE, IF_END, IF_BEGIN, BREAK, IF_END, APPLY,
                WHILE_END, IF_BEGIN, BREAK, IF_END, FOR_END, RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, WHILE_BEGIN, ASSIGN, IF_BEGIN, BREAK, IF_END,
                IF_BEGIN, PASS, IF_END, WHILE_END, METHOD_END, METHOD_BEGIN, FOR_BEGIN, APPLY, IF_BEGIN, BREAK, IF_END, IF_BEGIN, PASS, IF_END,
                FOR_END, METHOD_END, IF_BEGIN, APPLY, APPLY, APPLY, APPLY, APPLY, IF_END);

        collector.testFile("ExceptionGroupTest.py").testSourceCoverage().testTokenSequence(METHOD_BEGIN, METHOD_BEGIN, ASSIGN, LIST, APPLY, APPLY,
                APPLY, APPLY, IF_BEGIN, RAISE, APPLY, IF_END, METHOD_END, TRY_BEGIN, APPLY, EXCEPT_GROUP_BEGIN, APPLY, EXCEPT_GROUP_END,
                EXCEPT_GROUP_BEGIN, APPLY, EXCEPT_GROUP_END, EXCEPT_GROUP_BEGIN, APPLY, EXCEPT_GROUP_END, TRY_END, METHOD_END, METHOD_BEGIN,
                METHOD_BEGIN, ASSIGN, LIST, APPLY, APPLY, ASSIGN, LIST, APPLY, TRY_BEGIN, RAISE, APPLY, EXCEPT_BEGIN, APPLY, RAISE, APPLY, EXCEPT_END,
                TRY_END, METHOD_END, TRY_BEGIN, APPLY, EXCEPT_GROUP_BEGIN, APPLY, EXCEPT_GROUP_END, EXCEPT_GROUP_BEGIN, APPLY, EXCEPT_GROUP_END,
                EXCEPT_GROUP_BEGIN, APPLY, EXCEPT_GROUP_END, EXCEPT_GROUP_BEGIN, APPLY, EXCEPT_GROUP_END, TRY_END, METHOD_END, IF_BEGIN, APPLY, APPLY,
                IF_END);

        collector.testFile("TypeAliasTest.py").testSourceCoverage().testTokenSequence(IMPORT, TYPE_ALIAS, TYPE_ALIAS, METHOD_BEGIN, ASSIGN, LIST,
                ASSIGN, METHOD_BEGIN, FOR_BEGIN, APPLY, FOR_END, METHOD_END, APPLY, RETURN, METHOD_END, METHOD_BEGIN, TYPE_ALIAS, TYPE_ALIAS, LIST,
                ASSIGN, LIST, LIST, LIST, ASSIGN, LAMBDA, LAMBDA, APPLY, RETURN, METHOD_END, IF_BEGIN, APPLY, APPLY, IF_END);

        collector.testFile("ComprehensionsTest.py").testSourceCoverage().testTokenSequence(METHOD_BEGIN, ASSIGN, LIST, ASSIGN, LIST_COMPREHENSION,
                RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, LIST, ASSIGN, LIST_COMPREHENSION, RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, LIST, LIST,
                LIST, LIST, ASSIGN, LIST_COMPREHENSION, RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, LIST, ASSIGN, SET_COMPREHENSION, RETURN, METHOD_END,
                METHOD_BEGIN, ASSIGN, LIST, ASSIGN, SET_COMPREHENSION, RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, LIST, ASSIGN, LIST, ASSIGN,
                DICT_COMPREHENSION, APPLY, RETURN, METHOD_END, METHOD_BEGIN, ASSIGN, LIST, ASSIGN, LIST, ASSIGN, DICT_COMPREHENSION, APPLY, RETURN,
                METHOD_END);
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreLinesByPrefix("#");
        collector.ignoreEmptyLines();
        collector.ignoreLinesByPrefix("'''");
        collector.ignoreLinesByPrefix("\"\"\"");
    }
}
