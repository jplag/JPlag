package de.jplag.java.babylon;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.java.babylon.tokenizer.TokenizerLoader;
import de.jplag.java.babylon.tokenizer.impl.FullBabylonTokenizer;
import de.jplag.java.babylon.transformer.TransformationPipeline;
import de.jplag.java.babylon.transformer.TransformationStepLoader;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

class BabylonOptions extends LanguageOptions {
    private static final String ERROR_TRANSFORMATION_NOT_FOUND = "The selected transformation %s could not be found. Available transformations: %s";
    private static final String ERROR_NOT_ENOUGH_TRANSFORMATIONS = "Specify at least 1 transformation. Available transformations: %s";
    private static final String ERROR_TOKENIZER_NOT_FOUND = "The selected tokenizer %s could not be found. Available tokenizers: %s";
    private static final String ERROR_NO_TOKENIZER = "Specify a tokenizer. Available tokenizers: %s";
    private static final char LIST_SEPARATOR = ',';
    private static final String OPTION_DESCRIPTION_TRANSFORMATIONS = "The languages that should be used. This is a '" + LIST_SEPARATOR
            + "' separated list";
    private static final String OPTION_DESCRIPTION_TOKENIZER = "The tokenizer that should be used";
    private static final Pattern LIST_SEPARATOR_PATTERN = Pattern.compile("\\s*" + Pattern.quote(String.valueOf(LIST_SEPARATOR)) + "\\s*");

    private final LanguageOption<String> transformations = createOption(OptionType.string(), "transformations", OPTION_DESCRIPTION_TRANSFORMATIONS);
    private final LanguageOption<String> tokenizerName = createDefaultOption(OptionType.string(), "tokenizer", OPTION_DESCRIPTION_TOKENIZER,
            FullBabylonTokenizer.IDENTIFIER);

    public List<String> getTransformations() {
        @Nullable
        String transformationNames = transformations.getValue();
        if (transformationNames == null) {
            throw new IllegalArgumentException(
                    String.format(ERROR_NOT_ENOUGH_TRANSFORMATIONS, TransformationStepLoader.getAllAvailableTransformationStepIdentifiers()));
        }

        return Arrays.asList(LIST_SEPARATOR_PATTERN.split(transformationNames));
    }

    public LanguageOption<String> getTransformationNames() {
        return this.transformations;
    }

    private volatile @Nullable TransformationPipeline pipeline = null;

    public TransformationPipeline getTransformationPipeline() {
        if (this.pipeline == null) {
            synchronized (this) {
                if (this.pipeline == null) {
                    List<TransformationPipeline.Step> steps = getTransformations().stream()
                            .map(name -> TransformationStepLoader.getTransformationStep(name)
                                    .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_TRANSFORMATION_NOT_FOUND, name,
                                            TransformationStepLoader.getAllAvailableTransformationStepIdentifiers()))))
                            .toList();

                    if (steps.isEmpty()) {
                        throw new IllegalArgumentException(String.format(ERROR_NOT_ENOUGH_TRANSFORMATIONS,
                                TransformationStepLoader.getAllAvailableTransformationStepIdentifiers()));
                    }

                    this.pipeline = new TransformationPipeline(steps);
                }
            }
        }
        return this.pipeline;
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
}
