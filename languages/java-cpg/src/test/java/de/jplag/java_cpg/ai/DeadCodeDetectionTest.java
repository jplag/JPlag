package de.jplag.java_cpg.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.fraunhofer.aisec.cpg.ConfigurationException;
import de.fraunhofer.aisec.cpg.InferenceConfiguration;
import de.fraunhofer.aisec.cpg.TranslationConfiguration;
import de.fraunhofer.aisec.cpg.TranslationManager;
import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.frontends.java.JavaLanguage;
import de.fraunhofer.aisec.cpg.graph.Component;
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration;
import de.fraunhofer.aisec.cpg.passes.ControlDependenceGraphPass;
import de.fraunhofer.aisec.cpg.passes.DFGPass;
import de.fraunhofer.aisec.cpg.passes.DynamicInvokeResolver;
import de.fraunhofer.aisec.cpg.passes.EvaluationOrderGraphPass;
import de.fraunhofer.aisec.cpg.passes.FilenameMapper;
import de.fraunhofer.aisec.cpg.passes.ImportResolver;
import de.fraunhofer.aisec.cpg.passes.JavaExternalTypeHierarchyResolver;
import de.fraunhofer.aisec.cpg.passes.JavaImportResolver;
import de.fraunhofer.aisec.cpg.passes.Pass;
import de.fraunhofer.aisec.cpg.passes.ProgramDependenceGraphPass;
import de.fraunhofer.aisec.cpg.passes.ReplaceCallCastPass;
import de.fraunhofer.aisec.cpg.passes.SymbolResolver;
import de.fraunhofer.aisec.cpg.passes.TypeHierarchyResolver;
import de.fraunhofer.aisec.cpg.passes.TypeResolver;
import de.jplag.ParsingException;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.VariableStore;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

/**
 * Test that only uses the CPG library.
 * @author ujiqk
 * @version 1.0
 */
class DeadCodeDetectionTest {

    public static JavaObject getMainObject(@NotNull AbstractInterpretation interpretation) {
        VariableStore variableStore = interpretation.getVariables();
        return (JavaObject) Objects.requireNonNull(variableStore.getVariable("Main")).getValue();
    }

    @NotNull
    public static AbstractInterpretation interpretFromResource(String resourceDir) throws ParsingException, InterruptedException {
        ClassLoader classLoader = DeadCodeDetectionTest.class.getClassLoader();
        File submissionsRoot = new File(Objects.requireNonNull(classLoader.getResource(resourceDir)).getFile());
        Set<File> submissionDirectories = Set.of(submissionsRoot);
        TranslationResult result = translate(submissionDirectories);
        AbstractInterpretation interpretation = new AbstractInterpretation(new VisitedLinesRecorder(), true);

        Component comp = result.getComponents().getFirst();
        for (TranslationUnitDeclaration translationUnit : comp.getTranslationUnits()) {
            Assertions.assertNotNull(translationUnit.getName().getParent());
            if (translationUnit.getName().getParent().getLocalName().endsWith("Main") || comp.getTranslationUnits().size() == 1) {
                interpretation.runMain(translationUnit);
            }
        }
        return interpretation;
    }

    static TranslationResult translate(@NotNull Set<File> files) throws ParsingException, InterruptedException {
        InferenceConfiguration inferenceConfiguration = InferenceConfiguration.builder().inferRecords(true).inferDfgForUnresolvedCalls(true).build();
        TranslationResult translationResult;
        try {
            TranslationConfiguration.Builder configBuilder = new TranslationConfiguration.Builder().inferenceConfiguration(inferenceConfiguration)
                    .sourceLocations(files.toArray(new File[] {})).registerLanguage(new JavaLanguage());
            List<Class<? extends Pass<?>>> passClasses = new ArrayList<>(
                    List.of(TypeResolver.class, TypeHierarchyResolver.class, JavaExternalTypeHierarchyResolver.class, JavaImportResolver.class,
                            ImportResolver.class, SymbolResolver.class, DynamicInvokeResolver.class, FilenameMapper.class, ReplaceCallCastPass.class,
                            EvaluationOrderGraphPass.class, ControlDependenceGraphPass.class, ProgramDependenceGraphPass.class, DFGPass.class));
            for (Class<? extends Pass<?>> passClass : passClasses) {
                configBuilder.registerPass(getKClass(passClass));
            }
            translationResult = TranslationManager.builder().config(configBuilder.build()).build().analyze().get();
        } catch (ExecutionException | ConfigurationException e) {
            throw new ParsingException(List.copyOf(files).getFirst(), e);
        }
        return translationResult;
    }

    @NotNull
    private static <T extends Pass<?>> KClass<T> getKClass(Class<T> javaPassClass) {
        return JvmClassMappingKt.getKotlinClass(javaPassClass);
    }

    @NotNull
    private static java.net.URI getURI(@NotNull AbstractInterpretation interpretation, @NotNull String fileName) {
        VisitedLinesRecorder recorder = getVisitedLinesRecorder(interpretation);
        var nonVisited = recorder.getNonVisitedLines();
        for (var uri : nonVisited.keySet()) {
            if (uri.getPath().endsWith(fileName)) {
                return uri;
            }
        }
        throw new RuntimeException("URI not found for " + fileName);
    }

    @NotNull
    private static VisitedLinesRecorder getVisitedLinesRecorder(@NotNull AbstractInterpretation interpretation) {
        try {
            java.lang.reflect.Field field = AbstractInterpretation.class.getDeclaredField("visitedLinesRecorder");
            field.setAccessible(true);
            return (VisitedLinesRecorder) field.get(interpretation);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * a simple test with the main function only
     */
    @Test
    void testSimple() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/simple");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue());
    }

    /**
     * a simple test with the main function calling another function.
     */
    @Test
    void testSimple2() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/simple2");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());
        assertFalse(((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());
    }

    /**
     * a slightly more complex test with the main function calling other functions. with for loop and throw exception.
     */
    @Test
    void testSimple3() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/simple3");
        JavaObject main = getMainObject(interpretation);
        assertEquals(1, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());
        assertEquals(2, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue());
    }

    /**
     * simple switch test
     */
    @Disabled("Disabled due to containing break statements not yet supported")
    @Test
    void testSwitch() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/switch");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // z
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple switch test
     */
    @Test
    void testSwitch2() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/switch2");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // z
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simplest loop test
     */
    @Test
    void testLoop() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/loop");
        JavaObject main = getMainObject(interpretation);
        assertEquals(500, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue()); // z
        assertFalse(((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());
        assertFalse(((INumberValue) main.accessField("result3", new Type(Type.TypeEnum.INT))).getInformation());
    }

    /**
     * simplest for each loop test
     */
    @Test
    void testForEach() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/forEach");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());  // z
        assertTrue(((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());  // y
    }

    /**
     * Test creating a new class instance.
     */
    @Test
    void testNewClass() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/new");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

    /**
     * test if without else
     */
    @Test
    void testIf() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/if");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * test if with an else and another if inside the else block.
     */
    @Test
    void testNestedIf() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/nestedIf");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(50, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * test if with else-if and else.
     */
    @Test
    void testIfElse() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/ifElse");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * test if with 2x else-if and else.
     */
    @Test
    void testIfElse2x() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/ifElse2");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * test if with || in condition
     */
    @Test
    void testIfOr() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/ifOr");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * test if with && and || in condition
     */
    @Test
    void testIfAnd() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/ifAnd");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * test undetermined exception throw
     */
    @Test
    void testException() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/exception");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple enum test
     */
    @Test
    void testEnum() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/enum");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());
        assertFalse(((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());
    }

    /**
     * simple hashmap test
     */
    @Test
    void testHashMap() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/map");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

    /**
     * simple HashSet/TreeSet test.
     */
    @Test
    void testSet() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/set");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

    /**
     * Test the programming course final project: QueensFarming.
     */
    @Test
    @Disabled("Disabled due to containing break statements not yet supported")
    void testQueensFarming() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/complex");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

    /**
     * Test another programming course final project.
     */
    @Test
    void testTrafficSym() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/complex2");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

    /**
     * simple break statement test.
     */
    @Disabled("Disabled due to containing break statements not yet supported")
    @Test
    void testBreak() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/break");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

    /**
     * simple try/catch test
     */
    @Test
    void testTryCatch() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/try");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(101, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * a simple try /catch test with throw inside called method.
     */
    @Test
    void testTryCatch2() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/try2");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertEquals(250, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(200, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple try/catch test with nothing thrown.
     */
    @Test
    void testTryCatch3() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/try3");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertEquals(400, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(200, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple stream test.
     */
    @Test
    void testStream() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/stream");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertEquals(100, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple array test.
     */
    @Test
    void testArray() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/arrayInit");
        JavaObject main = getMainObject(interpretation);
        assertEquals(24, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple test for ConditionalExpressions (a?b:c).
     */
    @Test
    @Disabled("Disabled due to ConditionalExpressions not yet supported")
    void testConditional() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        Value.setUsedFloatAiType(FloatAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/conditional");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

    /**
     * simple array test
     */
    @Test
    void testMCEinClassField() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/simpleMCEinClassField");
        JavaObject main = getMainObject(interpretation);
        assertEquals(43, ((INumberValue) main.accessField("x", new Type(Type.TypeEnum.INT))).getValue());
    }

    /**
     * a simple test for a while with variable assignment in the condition.
     */
    @Test
    void testWhileAssign() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/whileAssign");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());
        assertFalse(((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());
    }

    /**
     * simple test for while inside while.
     */
    @Test
    void testNestedWhile() throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        Value.setUsedFloatAiType(FloatAiType.DEFAULT);
        Value.setUsedStringAiType(StringAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource("java/ai/nestedWhile");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());
        assertEquals(1, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue());
    }

    /**
     * a simple test for a return statement inside if.
     */
    @Test
    @Disabled
    void testReturnInIf() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/returnInIf");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // x
        assertEquals(200, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // 2*y
        assertEquals(500, ((INumberValue) main.accessField("result3", new Type(Type.TypeEnum.INT))).getValue()); // z
    }

    /**
     * a simple test for return statements only inside if.
     */
    @Test
    @Disabled
    void testReturnInIf2() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/returnInIf2");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // x
        assertEquals(200, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // 2*y
        assertEquals(500, ((INumberValue) main.accessField("result3", new Type(Type.TypeEnum.INT))).getValue()); // z
    }

    /**
     * a simple test for return statements inside two nested ifs.
     */
    @Test
    @Disabled
    void testReturnInIf2x() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/returnInIf2x");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // x
        assertEquals(700, ((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // 2*y
        assertEquals(500, ((INumberValue) main.accessField("result3", new Type(Type.TypeEnum.INT))).getValue()); // z
    }

    /**
     * a simple test for a return statement inside a while loop.
     */
    @Test
    @Disabled
    void testReturnInWhile() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/returnInWhile");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // x
        assertFalse(((INumberValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());          // y
        assertEquals(25, ((INumberValue) main.accessField("result3", new Type(Type.TypeEnum.INT))).getValue()); // z
    }

    @Test
    void testDeadCode2() throws ParsingException, InterruptedException {    // code after return
        AbstractInterpretation interpretation = interpretFromResource("java/ai/deadCode2");
        JavaObject main = getMainObject(interpretation);
        assertEquals(1, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());

        VisitedLinesRecorder recorder = getVisitedLinesRecorder(interpretation);
        assertTrue(recorder.checkIfCompletelyDead(getURI(interpretation, "Main.java"), 9, 9));
    }

    @Test
    void testDeadCode3() throws ParsingException, InterruptedException {    // unused method
        AbstractInterpretation interpretation = interpretFromResource("java/ai/deadCode3");
        JavaObject main = getMainObject(interpretation);
        assertEquals(1, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());

        VisitedLinesRecorder recorder = getVisitedLinesRecorder(interpretation);
        assertTrue(recorder.checkIfCompletelyDead(getURI(interpretation, "Main.java"), 10, 12));
    }

    @Test
    void testDeadCode5() throws ParsingException, InterruptedException {    // dead class
        AbstractInterpretation interpretation = interpretFromResource("java/ai/deadCode5");
        JavaObject main = getMainObject(interpretation);
        assertEquals(1, ((INumberValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());

        VisitedLinesRecorder recorder = getVisitedLinesRecorder(interpretation);
        // DeadClass on lines 11-15
        assertTrue(recorder.checkIfCompletelyDead(getURI(interpretation, "Main.java"), 11, 15));
    }

    @Test
    void testDeadCode11() throws ParsingException, InterruptedException {    // dead code in constructor and dead class
        AbstractInterpretation interpretation = interpretFromResource("java/ai/deadCode11");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        VisitedLinesRecorder recorder = getVisitedLinesRecorder(interpretation);
        // DeadClass on lines 20-32
        assertTrue(recorder.checkIfCompletelyDead(getURI(interpretation, "Main.java"), 20, 32));
    }

    @Test
    void testInheritance() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/inheritance");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

}
