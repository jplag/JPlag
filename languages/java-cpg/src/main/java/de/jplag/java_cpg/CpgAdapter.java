package de.jplag.java_cpg;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import de.fraunhofer.aisec.cpg.*;
import de.fraunhofer.aisec.cpg.frontends.java.JavaLanguage;
import de.fraunhofer.aisec.cpg.passes.*;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.passes.*;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

/**
 * This class handles the transformation of files of code to a token list.
 */
public class CpgAdapter {

    private List<Token> tokenList;
    private boolean reorderingEnabled = true;

    /**
     * Constructor for CpgAdapter.
     * @param transformations a list of {@link GraphTransformation}s
     */
    public CpgAdapter(GraphTransformation... transformations) {
        addTransformations(transformations);
    }

    /* package-private */ List<Token> adapt(Set<File> files, boolean normalize) throws ParsingException {
        assert !files.isEmpty();

        if (!normalize) {
            clearTransformations();
            setReorderingEnabled(false);
        }
        TranslationResult translationResult = translate(files);

        return tokenList;
    }

    /* package-private */ TranslationResult translate(Set<File> files) throws ParsingException {
        InferenceConfiguration inferenceConfiguration = InferenceConfiguration.builder().guessCastExpressions(true).inferRecords(true)
                .inferDfgForUnresolvedCalls(true).build();

        TranslationResult translationResult;
        TokenizationPass.Companion.setCallback(CpgAdapter.this::setTokenList);
        try {
            TranslationConfiguration.Builder configBuilder = new TranslationConfiguration.Builder().inferenceConfiguration(inferenceConfiguration)
                    .sourceLocations(files.toArray(new File[] {})).registerLanguage(new JavaLanguage());

            List<Class<? extends Pass<?>>> passClasses = new ArrayList<>(
                    List.of(TypeResolver.class, TypeHierarchyResolver.class, ImportResolver.class, SymbolResolver.class, FixAstPass.class,
                            DynamicInvokeResolver.class, FilenameMapper.class, AstTransformationPass.class, EvaluationOrderGraphPass.class,  // creates
                                                                                                                                             // EOG
                            DfgSortPass.class, CpgTransformationPass.class, TokenizationPass.class));

            if (!reorderingEnabled)
                passClasses.remove(DfgSortPass.class);

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
     * Registers the given transformations to be applied in the transformation step.
     * @param transformations the transformations
     */
    public void addTransformations(GraphTransformation[] transformations) {
        Arrays.stream(transformations).forEach(this::addTransformation);
    }

    /**
     * Adds a transformation at the end of its respective ATransformationPass.
     * @param transformation a {@link GraphTransformation}
     */
    public void addTransformation(GraphTransformation transformation) {
        switch (transformation.getPhase()) {
            case OBLIGATORY -> PrepareTransformationPass.registerTransformation(transformation);
            case AST_TRANSFORM -> AstTransformationPass.registerTransformation(transformation);
            case CPG_TRANSFORM -> CpgTransformationPass.registerTransformation(transformation);
        }
    }

    /**
     * Clears all non-{@link ExecutionPhase#OBLIGATORY} transformations from the pipeline.
     */
    public void clearTransformations() {
        AstTransformationPass.clearTransformations();
        CpgTransformationPass.clearTransformations();
    }

    /**
     * Sets <code>reorderingEnabled</code>. If true, statements may be reordered.
     * @param enabled value for reorderingEnabled.
     */
    public void setReorderingEnabled(boolean enabled) {
        this.reorderingEnabled = enabled;
    }
}
