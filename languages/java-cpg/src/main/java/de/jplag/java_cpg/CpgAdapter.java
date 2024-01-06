package de.jplag.java_cpg;

import de.fraunhofer.aisec.cpg.*;
import de.fraunhofer.aisec.cpg.frontends.java.JavaLanguage;
import de.fraunhofer.aisec.cpg.passes.*;
import de.fraunhofer.aisec.cpg_vis_neo4j.Application;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.passes.CleanupTransformationPass;
import de.jplag.java_cpg.passes.TokenizationPass;
import de.jplag.java_cpg.passes.TransformationPass;
import de.jplag.java_cpg.transformation.GraphTransformation;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

import java.io.File;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * This class handles the transformation of files of code to a token list.
 */
public class CpgAdapter {

    private List<Token> tokenList;

    public CpgAdapter(GraphTransformation<?>... transformations) {
        addTransformations(transformations);
    }

    /* package-private */ List<Token> adapt(Set<File> files) throws ParsingException {
        assert !files.isEmpty();
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
            TranslationConfiguration translationConfiguration = new TranslationConfiguration.Builder()
                .inferenceConfiguration(inferenceConfiguration)
                .registerPass(getKClass(TypeResolver.class))
                .registerPass(getKClass(TypeHierarchyResolver.class))
                .registerPass(getKClass(ImportResolver.class))
                .registerPass(getKClass(SymbolResolver.class))
                .registerPass(getKClass(DFGPass.class))
                .registerPass(getKClass(DynamicInvokeResolver.class))
                .registerPass(getKClass(ControlFlowSensitiveDFGPass.class))
                .registerPass(getKClass(FilenameMapper.class))
                .registerPass(getKClass(TransformationPass.class))
                .registerPass(getKClass(EvaluationOrderGraphPass.class)) // creates EOG
                .registerPass(getKClass(CleanupTransformationPass.class))
                .registerPass(getKClass(TokenizationPass.class))
                .registerLanguage(new JavaLanguage())
                .sourceLocations(files.toArray(new File[]{})).build();

            translationResult = TranslationManager.builder().config(translationConfiguration).build().analyze().get();

        } catch (InterruptedException | ExecutionException | ConfigurationException e) {
            throw new ParsingException(List.copyOf(files).get(0), e);
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
}

