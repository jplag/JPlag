package de.jplag.java.babylon;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.java.babylon.tokenizer.TokenizerLoader;
import de.jplag.java.babylon.tokenizer.impl.FullBabylonTokenizer;
import de.jplag.java.babylon.transformer.TransformationStep;
import de.jplag.java.babylon.transformer.TransformationStepLoader;
import de.jplag.java.babylon.transformer.impl.AssertRemoveTransformer;
import de.jplag.java.babylon.transformer.impl.BlockNormalizeStep;
import de.jplag.java.babylon.transformer.impl.ConditionalExpressionDesugarTransformer;
import de.jplag.java.babylon.transformer.impl.ConstantPropagationStep;
import de.jplag.java.babylon.transformer.impl.CopyElisionTransformer;
import de.jplag.java.babylon.transformer.impl.DeadCodeEliminationTransformer;
import de.jplag.java.babylon.transformer.impl.EnhancedForDesugarTransformer;
import de.jplag.java.babylon.transformer.impl.ForDesugarTransformer;
import de.jplag.java.babylon.transformer.impl.IfFuseTransformer;
import de.jplag.java.babylon.transformer.impl.InliningStep;
import de.jplag.java.babylon.transformer.impl.OptionalElisionTransformer;
import de.jplag.java.babylon.transformer.impl.StreamFuseTransformer;
import de.jplag.java.babylon.transformer.impl.SwitchExpressionDesugarTransformer;
import de.jplag.java.babylon.transformer.impl.TryWithResourcesDesugarTransformer;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

class BabylonOptions extends LanguageOptions {
    private static final String ERROR_TRANSFORMATION_NOT_FOUND = "The selected transformation %s could not be found. Available transformations: %s";
    private static final String ERROR_TOKENIZER_NOT_FOUND = "The selected tokenizer %s could not be found. Available tokenizers: %s";
    private static final String ERROR_NO_TOKENIZER = "Specify a tokenizer. Available tokenizers: %s";
    private static final char LIST_SEPARATOR = ',';
    private static final String OPTION_DESCRIPTION_TRANSFORMATIONS = "The languages that should be used. This is a '" + LIST_SEPARATOR
            + "' separated list";
    private static final String OPTION_DESCRIPTION_TOKENIZER = "The tokenizer that should be used";
    private static final Pattern LIST_SEPARATOR_PATTERN = Pattern.compile("\\s*" + Pattern.quote(String.valueOf(LIST_SEPARATOR)) + "\\s*");

    private static final String DEFAULT_TRANSFORMATIONS = String.join(", ", AssertRemoveTransformer.IDENTIFIER,
            TryWithResourcesDesugarTransformer.IDENTIFIER, CopyElisionTransformer.IDENTIFIER, StreamFuseTransformer.IDENTIFIER,
            EnhancedForDesugarTransformer.IDENTIFIER, ForDesugarTransformer.IDENTIFIER, OptionalElisionTransformer.IDENTIFIER,
            ConditionalExpressionDesugarTransformer.IDENTIFIER, SwitchExpressionDesugarTransformer.IDENTIFIER, ConstantPropagationStep.IDENTIFIER,
            IfFuseTransformer.IDENTIFIER, InliningStep.IDENTIFIER, BlockNormalizeStep.IDENTIFIER, ConstantPropagationStep.IDENTIFIER,
            CopyElisionTransformer.IDENTIFIER, DeadCodeEliminationTransformer.IDENTIFIER, CopyElisionTransformer.IDENTIFIER,
            DeadCodeEliminationTransformer.IDENTIFIER, InliningStep.IDENTIFIER);

    private final LanguageOption<String> transformations = createDefaultOption(OptionType.string(), "transformations",
            OPTION_DESCRIPTION_TRANSFORMATIONS, DEFAULT_TRANSFORMATIONS);
    private final LanguageOption<String> tokenizerName = createDefaultOption(OptionType.string(), "tokenizer", OPTION_DESCRIPTION_TOKENIZER,
            FullBabylonTokenizer.IDENTIFIER);

    public List<String> getTransformations() {
        @Nullable
        String transformationNames = transformations.getValue();
        if (transformationNames == null) {
            throw new IllegalArgumentException(
                    String.format(ERROR_TRANSFORMATION_NOT_FOUND, "null", TransformationStepLoader.getAllAvailableTransformationStepIdentifiers()));
        }

        return LIST_SEPARATOR_PATTERN.splitAsStream(transformationNames).filter(s -> !s.isBlank()).map(String::trim).toList();
    }

    public LanguageOption<String> getTransformationNames() {
        return this.transformations;
    }

    private volatile @Nullable List<? extends TransformationStep<?>> pipelineSteps = null;

    public List<? extends TransformationStep<?>> getPipelineSteps() {
        if (this.pipelineSteps == null) {
            synchronized (this) {
                if (this.pipelineSteps == null) {
                    this.pipelineSteps = getTransformations().stream()
                            .map(name -> TransformationStepLoader.getTransformationStep(name)
                                    .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_TRANSFORMATION_NOT_FOUND, name,
                                            TransformationStepLoader.getAllAvailableTransformationStepIdentifiers()))))
                            .toList();
                }
            }
        }
        return this.pipelineSteps;
    }

    public TransformationPipeline getTransformationPipeline() {
        return new TransformationPipeline(getPipelineSteps());
    }

    public LanguageOption<String> getTokenizerName() {
        return this.tokenizerName;
    }

    private volatile @Nullable BabylonTokenizer.Provider tokenizer = null;

    public BabylonTokenizer.Provider getTokenizer() {
        if (tokenizer == null) {
            synchronized (this) {
                if (this.tokenizer == null) {
                    @Nullable
                    String tokenizerName = this.tokenizerName.getValue();

                    if (tokenizerName == null) {
                        throw new IllegalArgumentException(String.format(ERROR_NO_TOKENIZER, TokenizerLoader.getAllAvailableTokenizerIdentifiers()));
                    }

                    this.tokenizer = TokenizerLoader.getTokenizer(tokenizerName).orElseThrow(() -> new IllegalArgumentException(
                            String.format(ERROR_TOKENIZER_NOT_FOUND, tokenizerName, TokenizerLoader.getAllAvailableTokenizerIdentifiers())));
                }
            }
        }
        return this.tokenizer;
    }

    public void clearCaches() {
        synchronized (this) {
            pipelineSteps = null;
            tokenizer = null;
        }
    }
}
