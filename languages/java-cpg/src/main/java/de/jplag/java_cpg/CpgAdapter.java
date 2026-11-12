package de.jplag.java_cpg;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
import de.jplag.java_cpg.passes.GraphMutationPassConfiguration;
import de.jplag.java_cpg.passes.PrepareTransformationPass;
import de.jplag.java_cpg.passes.TokenizationPass;
import de.jplag.java_cpg.passes.TransformationPassConfiguration;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase;
import de.jplag.java_cpg.transformation.operations.GraphMutationContext;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

/**
 * This class handles the transformation of files of code to a token list.
 */
public class CpgAdapter {

    private final Collection<GraphTransformation> transformations = new LinkedHashSet<>();
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
        TranslationResult translate = translate(files, snapshot(normalize));

        return (List<Token>) translate.getScratch().getOrDefault("tokenList", List.of());
    }

    /**
     * Adds a transformation once, preserving its first registration order within its phase.
     * @param transformation a {@link GraphTransformation}
     */
    public synchronized void addTransformation(GraphTransformation transformation) {
        transformations.add(Objects.requireNonNull(transformation));
    }

    /**
     * Registers the given transformations to be applied in the transformation step.
     * @param transformations the transformations
     */
    public synchronized void addTransformations(GraphTransformation[] transformations) {
        Arrays.stream(transformations).forEach(this::addTransformation);
    }

    /**
     * Clears all non-{@link ExecutionPhase#OBLIGATORY} transformations from the pipeline.
     */
    public synchronized void clearTransformations() {
        transformations.removeIf(transformation -> transformation.phase() != ExecutionPhase.OBLIGATORY);
    }

    private <T extends Pass<?>> KClass<T> getKClass(Class<T> javaPassClass) {
        return JvmClassMappingKt.getKotlinClass(javaPassClass);
    }

    /**
     * Sets {@code reorderingEnabled}. If true, statements may be reordered.
     * @param enabled value for reorderingEnabled.
     */
    public synchronized void setReorderingEnabled(boolean enabled) {
        this.reorderingEnabled = enabled;
    }

    /* package-private */ synchronized TransformationSnapshot snapshot(boolean normalize) {
        return new TransformationSnapshot(
                transformations.stream().filter(transformation -> normalize || transformation.phase() == ExecutionPhase.OBLIGATORY).toList(),
                normalize && reorderingEnabled);
    }

    /* package-private */ record TransformationSnapshot(List<GraphTransformation> transformations, boolean reorderingEnabled) {
        TransformationSnapshot {
            transformations = List.copyOf(transformations);
        }

        TransformationPassConfiguration forPhase(ExecutionPhase phase, GraphMutationContext mutationContext) {
            return new TransformationPassConfiguration(transformations.stream().filter(transformation -> transformation.phase() == phase).toList(),
                    mutationContext);
        }
    }

    /* package-private */ TranslationResult translate(Set<File> files) throws ParsingException, InterruptedException {
        return translate(files, snapshot(true));
    }

    private TranslationResult translate(Set<File> files, TransformationSnapshot snapshot) throws ParsingException, InterruptedException {
        InferenceConfiguration inferenceConfiguration = InferenceConfiguration.builder().inferRecords(true).inferDfgForUnresolvedCalls(true).build();

        TranslationResult translationResult;
        GraphMutationContext mutationContext = new GraphMutationContext();
        try {
            TranslationConfiguration.Builder configBuilder = new TranslationConfiguration.Builder().inferenceConfiguration(inferenceConfiguration)
                    .sourceLocations(files.toArray(new File[] {})).registerLanguage(new JavaLanguage());

            configBuilder.configurePass(getKClass(PrepareTransformationPass.class), snapshot.forPhase(ExecutionPhase.OBLIGATORY, mutationContext));
            configBuilder.configurePass(getKClass(AstTransformationPass.class), snapshot.forPhase(ExecutionPhase.AST_TRANSFORM, mutationContext));
            configBuilder.configurePass(getKClass(CpgTransformationPass.class), snapshot.forPhase(ExecutionPhase.CPG_TRANSFORM, mutationContext));
            configBuilder.configurePass(getKClass(DfgSortPass.class), new GraphMutationPassConfiguration(mutationContext));

            Collection<Class<? extends Pass<?>>> passClasses = new ArrayList<>(List.of(TypeResolver.class, TypeHierarchyResolver.class,
                    ImportResolver.class, SymbolResolver.class, PrepareTransformationPass.class, FixAstPass.class, DynamicInvokeResolver.class,
                    FilenameMapper.class, AstTransformationPass.class, EvaluationOrderGraphPass.class,  // creates
                    // EOG
                    DfgSortPass.class, CpgTransformationPass.class, TokenizationPass.class));

            if (!snapshot.reorderingEnabled())
                passClasses.remove(DfgSortPass.class);

            for (Class<? extends Pass<?>> passClass : passClasses) {
                configBuilder.registerPass(getKClass(passClass));
            }

            // Cleanup belongs to analysis completion, even if the waiting caller is interrupted.
            translationResult = TranslationManager.builder().config(configBuilder.build()).build().analyze()
                    .whenComplete((result, failure) -> mutationContext.close()).get();

        } catch (InterruptedException e) {
            throw e;
        } catch (ConfigurationException e) {
            mutationContext.close();
            throw new ParsingException(List.copyOf(files).getFirst(), e);
        } catch (ExecutionException e) {
            throw new ParsingException(List.copyOf(files).getFirst(), e);
        }
        return translationResult;
    }
}
