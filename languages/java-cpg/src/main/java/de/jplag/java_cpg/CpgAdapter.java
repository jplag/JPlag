package de.jplag.java_cpg;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import de.fraunhofer.aisec.cpg.ConfigurationException;
import de.fraunhofer.aisec.cpg.InferenceConfiguration;
import de.fraunhofer.aisec.cpg.TranslationConfiguration;
import de.fraunhofer.aisec.cpg.TranslationManager;
import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.frontends.java.JavaLanguage;
import de.fraunhofer.aisec.cpg.passes.DynamicInvokeResolver;
import de.fraunhofer.aisec.cpg.passes.EvaluationOrderGraphPass;
import de.fraunhofer.aisec.cpg.passes.FilenameMapper;
import de.fraunhofer.aisec.cpg.passes.ImportResolver;
import de.fraunhofer.aisec.cpg.passes.Pass;
import de.fraunhofer.aisec.cpg.passes.SymbolResolver;
import de.fraunhofer.aisec.cpg.passes.TypeHierarchyResolver;
import de.fraunhofer.aisec.cpg.passes.TypeResolver;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.passes.AstTransformationPass;
import de.jplag.java_cpg.passes.CpgTransformationPass;
import de.jplag.java_cpg.passes.DfgSortPass;
import de.jplag.java_cpg.passes.FixAstPass;
import de.jplag.java_cpg.passes.PrepareTransformationPass;
import de.jplag.java_cpg.passes.TokenizationPass;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

/**
 * This class handles the transformation of files of code to a token list.
 */
public class CpgAdapter {

    private boolean reorderingEnabled = true;

    /**
     * Constructs a new {@link CpgAdapter}.
     * @param transformations a list of {@link GraphTransformation}s
     */
    public CpgAdapter(GraphTransformation... transformations) {
        addTransformations(transformations);
    }

    @SuppressWarnings("unchecked")
    /* package-private */ List<Token> adapt(Set<File> files, boolean normalize) throws ParsingException, InterruptedException {
        assert !files.isEmpty();
        if (!normalize) {
            clearTransformations();
            setReorderingEnabled(false);
        }

        TranslationResult translate = translate(files);

        return (List<Token>) translate.getScratch().getOrDefault("tokenList", List.of());
    }

    /**
     * Adds a transformation at the end of its respective ATransformationPass.
     * @param transformation a {@link GraphTransformation}
     */
    public void addTransformation(GraphTransformation transformation) {
        switch (transformation.phase()) {
            case OBLIGATORY -> PrepareTransformationPass.registerTransformation(transformation);
            case AST_TRANSFORM -> AstTransformationPass.registerTransformation(transformation);
            case CPG_TRANSFORM -> CpgTransformationPass.registerTransformation(transformation);
        }
    }

    /**
     * Registers the given transformations to be applied in the transformation step.
     * @param transformations the transformations
     */
    public void addTransformations(GraphTransformation[] transformations) {
        Arrays.stream(transformations).forEach(this::addTransformation);
    }

    /**
     * Clears all non-{@link ExecutionPhase#OBLIGATORY} transformations from the pipeline.
     */
    public void clearTransformations() {
        AstTransformationPass.clearTransformations();
        CpgTransformationPass.clearTransformations();
    }

    private <T extends Pass<?>> KClass<T> getKClass(Class<T> javaPassClass) {
        return JvmClassMappingKt.getKotlinClass(javaPassClass);
    }

    /**
     * Sets <code>reorderingEnabled</code>. If true, statements may be reordered.
     * @param enabled value for reorderingEnabled.
     */
    public void setReorderingEnabled(boolean enabled) {
        this.reorderingEnabled = enabled;
    }

    /* package-private */ TranslationResult translate(Set<File> files) throws ParsingException, InterruptedException {
        InferenceConfiguration inferenceConfiguration = InferenceConfiguration.builder().inferRecords(true).inferDfgForUnresolvedCalls(true).build();

        TranslationResult translationResult;
        try {
            TranslationConfiguration.Builder configBuilder = new TranslationConfiguration.Builder().inferenceConfiguration(inferenceConfiguration)
                    .sourceLocations(files.toArray(new File[] {})).registerLanguage(new JavaLanguage());

            List<Class<? extends Pass<?>>> passClasses = new ArrayList<>(List.of(TypeResolver.class, TypeHierarchyResolver.class,
                    ImportResolver.class, SymbolResolver.class, PrepareTransformationPass.class, FixAstPass.class, DynamicInvokeResolver.class,
                    FilenameMapper.class, AstTransformationPass.class, EvaluationOrderGraphPass.class,  // creates
                    // EOG
                    DfgSortPass.class, CpgTransformationPass.class, TokenizationPass.class));

            if (!reorderingEnabled)
                passClasses.remove(DfgSortPass.class);

            for (Class<? extends Pass<?>> passClass : passClasses) {
                configBuilder.registerPass(getKClass(passClass));
            }

            translationResult = TranslationManager.builder().config(configBuilder.build()).build().analyze().get();

        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException | ConfigurationException e) {
            throw new ParsingException(List.copyOf(files).getFirst(), e);
        }
        return translationResult;
    }
}
