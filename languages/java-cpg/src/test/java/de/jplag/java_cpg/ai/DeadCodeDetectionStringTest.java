package de.jplag.java_cpg.ai;

import static de.jplag.java_cpg.ai.DeadCodeDetectionTest.getMainObject;
import static de.jplag.java_cpg.ai.DeadCodeDetectionTest.interpretFromResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.string.StringCharInclValue;
import de.jplag.java_cpg.ai.variables.values.string.StringRegexValue;
import de.jplag.java_cpg.ai.variables.values.string.regex.RegexChar;

/**
 * Test that only uses the CPG library. Specifically, tests different string analyses.
 * @author ujiqk
 * @version 1.0
 */
class DeadCodeDetectionStringTest {

    /**
     * simple test for character inclusion string analysis.
     */
    @Test
    void testString() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        Value.setUsedFloatAiType(FloatAiType.DEFAULT);
        Value.setUsedStringAiType(StringAiType.CHAR_INCLUSION);
        final AbstractInterpretation interpretation = interpretFromResource("java/ai/string");
        JavaObject main = getMainObject(interpretation);
        assertEquals(new HashSet<>(Set.of(' ', '!', 'D', 'e', 'H', 'h', 'J', 'l', ',', 'n', 'o')),
                ((StringCharInclValue) main.accessField("result", new Type(Type.TypeEnum.STRING))).getCertainContained());
        assertEquals(new HashSet<>(Set.of()), ((StringCharInclValue) main.accessField("result", new Type(Type.TypeEnum.STRING))).getMaybeContained());
        assertEquals(new HashSet<>(Set.of('J', 'O', 'H', 'N', ' ', 'D', 'E')),
                ((StringCharInclValue) main.accessField("result2", new Type(Type.TypeEnum.STRING))).getCertainContained());
        assertEquals(new HashSet<>(Set.of()),
                ((StringCharInclValue) main.accessField("result2", new Type(Type.TypeEnum.STRING))).getMaybeContained());
    }

    /**
     * simple test for regex string analysis.
     */
    @Test
    void testRegexString() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        Value.setUsedFloatAiType(FloatAiType.DEFAULT);
        Value.setUsedStringAiType(StringAiType.REGEX);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/string");
        JavaObject main = getMainObject(interpretation);
        assertEquals(
                new ArrayList<>(List.of(new RegexChar('H'), new RegexChar('e'), new RegexChar('l'), new RegexChar('l'), new RegexChar('o'),
                        new RegexChar(','), new RegexChar(' '), new RegexChar('J'), new RegexChar('o'), new RegexChar('h'), new RegexChar('n'),
                        new RegexChar(' '), new RegexChar('D'), new RegexChar('o'), new RegexChar('e'), new RegexChar('!'))),
                ((StringRegexValue) main.accessField("result", new Type(Type.TypeEnum.STRING))).getContentRegex());

        assertEquals(
                new ArrayList<>(List.of(new RegexChar('J'), new RegexChar('O'), new RegexChar('H'), new RegexChar('N'), new RegexChar(' '),
                        new RegexChar('D'), new RegexChar('O'), new RegexChar('E'))),
                ((StringRegexValue) main.accessField("result2", new Type(Type.TypeEnum.STRING))).getContentRegex());
    }

    @Test
    @Disabled("Disabled due to containing break statements not yet supported")
    void testRegexStringComplex() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        Value.setUsedFloatAiType(FloatAiType.DEFAULT);
        Value.setUsedStringAiType(StringAiType.REGEX);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/stringComplex");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

}
