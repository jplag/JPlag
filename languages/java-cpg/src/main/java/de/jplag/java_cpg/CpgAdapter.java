package de.jplag.java_cpg;

import de.fraunhofer.aisec.cpg.*;
import de.fraunhofer.aisec.cpg.frontends.cxx.CPPLanguage;
import de.fraunhofer.aisec.cpg.frontends.java.JavaLanguage;
import de.fraunhofer.aisec.cpg.passes.*;
import de.fraunhofer.aisec.cpg_vis_neo4j.Application;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.passes.CleanupTransformationPass;
import de.jplag.java_cpg.passes.DFGSortPass;
import de.jplag.java_cpg.passes.TokenizationPass;
import de.jplag.java_cpg.passes.TransformationPass;
import de.jplag.java_cpg.transformation.GraphTransformation;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * This class handles the transformation of files of code to a token list.
 */
public class CpgAdapter {

    private List<Token> tokenList;
    private boolean reorderingEnabled = true;

    public CpgAdapter(GraphTransformation<?>... transformations) {
        addTransformations(transformations);
    }

    /* package-private */ List<Token> adapt(Set<File> files, boolean normalize) throws ParsingException {
        assert !files.isEmpty();

        if (!normalize) {
            clearTransformations();
            setReorderingEnabled(false);
        }
        TranslationResult translationResult = translate(files);

        boolean doPushToNeo4j = false;
        if (doPushToNeo4j) {
            pushToNeo4j(translationResult);
        }
        return tokenList;
    }

    /* package-private */ TranslationResult translate(Set<File> files) throws ParsingException {
        InferenceConfiguration inferenceConfiguration = InferenceConfiguration.builder().guessCastExpressions(true).inferRecords(true).inferDfgForUnresolvedCalls(true).build();

        TranslationResult translationResult;
        TokenizationPass.Companion.setCallback(CpgAdapter.this::setTokenList);
        try {
            TranslationConfiguration.Builder configBuilder = new TranslationConfiguration.Builder()
                .inferenceConfiguration(inferenceConfiguration)
                .sourceLocations(files.toArray(new File[]{}))
                .registerLanguage(new JavaLanguage())
                .registerLanguage(new CPPLanguage());


            List<Class<? extends Pass<?>>> passClasses = new ArrayList<>(List.of(
                TypeResolver.class,
                TypeHierarchyResolver.class,
                ImportResolver.class,
                SymbolResolver.class,
                DynamicInvokeResolver.class,
                FilenameMapper.class,
                TransformationPass.class,
                EvaluationOrderGraphPass.class,  // creates EOG
                DFGSortPass.class,
                CleanupTransformationPass.class,
                TokenizationPass.class
            ));

            if (!reorderingEnabled) passClasses.remove(DFGSortPass.class);

            for (Class<? extends Pass<?>> passClass : passClasses) {
                configBuilder.registerPass(getKClass(passClass));
            }

            translationResult = TranslationManager.builder().config(configBuilder.build()).build().analyze().get();

        } catch (InterruptedException | ExecutionException | ConfigurationException e) {
            throw new ParsingException(List.copyOf(files).getFirst(), e);
        }
        return translationResult;
    }

    private <T extends Pass<?>> KClass<T> getKClass(Class<T> javaPassClass) {
        return JvmClassMappingKt.getKotlinClass(javaPassClass);
    }

    private void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    /**
     * Exports the given {@link TranslationResult} to Neo4j.
     * @param translationResult the {@link TranslationResult}
     */
    private static void pushToNeo4j(TranslationResult translationResult) {
        Application app = new Application();
        try {
            app.pushToNeo4j(translationResult);
        } catch (InterruptedException | ConnectException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers the given transformations to be applied in the transformation step.
     *
     * @param transformations the transformations
     */
    public void addTransformations(GraphTransformation<?>[] transformations) {
        Arrays.stream(transformations).forEach(t -> {
                switch (t.getPhase()) {
                    case PHASE_ONE -> TransformationPass.registerTransformation(t);
                    case PHASE_TWO -> CleanupTransformationPass.registerTransformation(t);
                }
            }
        );
    }

    public void clearTransformations() {
        TransformationPass.clearTransformations();
        CleanupTransformationPass.clearTransformations();
    }

    public void setReorderingEnabled(boolean enabled) {
        this.reorderingEnabled = enabled;
    }
}

