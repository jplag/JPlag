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
import de.jplag.java_cpg.ai.variables.values.numbers.IntValue;
import de.jplag.java_cpg.passes.AstTransformationPass;
import de.jplag.java_cpg.passes.CpgTransformationPass;
import de.jplag.java_cpg.passes.DfgSortPass;
import de.jplag.java_cpg.passes.FixAstPass;
import de.jplag.java_cpg.passes.PrepareTransformationPass;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

/**
 * Test that uses the CPG library and the existing java-cpg code.
 * @author ujiqk
 * @version 1.0
 */
class AbstractInterpretationTest {

    /**
     * a simple test with the main function only.
     */
    @Test
    void testSimple() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/simple");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());
        assertEquals(100, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue());
    }

    /**
     * a simple test with the main function calling another function.
     */
    @Test
    void testSimple2() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/simple2");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());
        assertFalse(((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());
    }

    /**
     * a slightly more complex test with the main function calling other functions. with for loop and throw exception.
     */
    @Test
    void testSimple3() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/simple3");
        JavaObject main = getMainObject(interpretation);
        assertEquals(1, ((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());
        assertEquals(2, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue());
    }

    /**
     * simple switch test
     */
    @Test
    @Disabled("test contains a switch statement, which is currently not supported")
    void testSwitch() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/switch");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertFalse(((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // z
        assertEquals(100, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple switch test
     */
    @Test
    void testSwitch2() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/switch2");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertFalse(((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // z
        assertEquals(100, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simplest loop test
     */
    @Test
    void testLoop() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/loop");
        JavaObject main = getMainObject(interpretation);
        assertTrue(((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());
        assertFalse(((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());
    }

    /**
     * simplest for each loop test
     */
    @Test
    void testForEach() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/forEach");
        JavaObject main = getMainObject(interpretation);
        assertFalse(((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getInformation());          // z
        assertEquals(100, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * test creating a new class instance
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
        assertEquals(400, ((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(100, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * test undetermined exception throw.
     */
    @Test
    void testException() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/exception");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(100, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple enum test
     */
    @Test
    void testEnum() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/enum");
        JavaObject main = getMainObject(interpretation);
        assertEquals(400, ((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());
        assertFalse(((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getInformation());
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

    @Test
    @Disabled("error in the dfg sort pass")
    void testQueensFarming() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/complex");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
    }

    @Test
    void testQueensFarming2() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/complex2");
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
        assertEquals(400, ((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(101, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * a simple try /catch test with throw inside called method.
     */
    @Test
    void testTryCatch2() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/try2");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertEquals(250, ((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(200, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    /**
     * simple try/catch test with nothing thrown.
     */
    @Test
    void testTryCatch3() throws ParsingException, InterruptedException {
        AbstractInterpretation interpretation = interpretFromResource("java/ai/try3");
        JavaObject main = getMainObject(interpretation);
        assertNotNull(main);
        assertEquals(400, ((IntValue) main.accessField("result", new Type(Type.TypeEnum.INT))).getValue());  // z
        assertEquals(200, ((IntValue) main.accessField("result2", new Type(Type.TypeEnum.INT))).getValue()); // y
    }

    private TranslationResult translate(@NotNull Set<File> files) throws ParsingException, InterruptedException {
        InferenceConfiguration inferenceConfiguration = InferenceConfiguration.builder().inferRecords(true).inferDfgForUnresolvedCalls(true).build();
        TranslationResult translationResult;
        try {
            TranslationConfiguration.Builder configBuilder = new TranslationConfiguration.Builder().inferenceConfiguration(inferenceConfiguration)
                    .sourceLocations(files.toArray(new File[] {})).registerLanguage(new JavaLanguage());
            List<Class<? extends Pass<?>>> passClasses = new ArrayList<>(List.of(TypeResolver.class, TypeHierarchyResolver.class,
                    JavaExternalTypeHierarchyResolver.class, JavaImportResolver.class, ImportResolver.class, SymbolResolver.class,
                    PrepareTransformationPass.class, FixAstPass.class, DynamicInvokeResolver.class, FilenameMapper.class, ReplaceCallCastPass.class,
                    AstTransformationPass.class, EvaluationOrderGraphPass.class, ControlDependenceGraphPass.class, ProgramDependenceGraphPass.class,
                    DfgSortPass.class, CpgTransformationPass.class));
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
    private <T extends Pass<?>> KClass<T> getKClass(Class<T> javaPassClass) {
        return JvmClassMappingKt.getKotlinClass(javaPassClass);
    }

    private JavaObject getMainObject(@NotNull AbstractInterpretation interpretation) {
        VariableStore variableStore = interpretation.getVariables();
        return (JavaObject) variableStore.getVariable("Main").getValue();
    }

    @NotNull
    private AbstractInterpretation interpretFromResource(String resourceDir) throws ParsingException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        File submissionsRoot = new File(Objects.requireNonNull(classLoader.getResource(resourceDir)).getFile());
        Set<File> submissionDirectories = Set.of(submissionsRoot);
        TranslationResult result = translate(submissionDirectories);
        AbstractInterpretation interpretation = new AbstractInterpretation(new VisitedLinesRecorder(), true);

        Component comp = result.getComponents().getFirst();
        for (TranslationUnitDeclaration translationUnit : comp.getTranslationUnits()) {
            Assertions.assertNotNull(translationUnit.getName().getParent());
            if (translationUnit.getName().getParent().getLocalName().endsWith("Main")) {
                interpretation.runMain(translationUnit);
            }
        }
        return interpretation;
    }

}
