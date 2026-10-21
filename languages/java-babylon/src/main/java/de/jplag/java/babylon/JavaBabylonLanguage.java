package de.jplag.java.babylon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.java.JavaLanguage;
import de.jplag.java.Parser;
import de.jplag.java.babylon.tokenizer.impl.HighLevelBabylonTokenizer;
import de.jplag.java.babylon.transformer.impl.LoweringStep;

import com.google.auto.service.AutoService;

/**
 * {@link Language} implementation for Java with support for {@link jdk.incubator.code.CodeTransformer}-based
 * transformations. <br>
 */
@AutoService(Language.class)
public class JavaBabylonLanguage extends JavaLanguage {
    private static final Logger logger = LoggerFactory.getLogger(JavaBabylonLanguage.class);
    private final BabylonOptions options = new BabylonOptions();

    @Override
    public String getName() {
        return super.getName() + " (Babylon)";
    }

    @Override
    public String getIdentifier() {
        return super.getIdentifier() + "-babylon";
    }

    @Override
    public int minimumTokenMatch() {
        if (options.getTransformations().contains(LoweringStep.IDENTIFIER)) {
            return 7;
        } else if (options.getTokenizerName().getValue().equals(HighLevelBabylonTokenizer.IDENTIFIER)) {
            return 11;
        } else {
            return 8;
        }
    }

    @Override
    protected Parser createParser() throws ParsingException {
        try {
            return new ParserBabylon(options.getTransformationPipeline(), options.getTokenizer());
        } catch (IllegalArgumentException e) {
            logger.error("Could not create parser", e);
            throw e;
        }
    }

    @Override
    public BabylonOptions getOptions() {
        return options;
    }

    @Override
    public boolean supportsNormalization() {
        return false;
    }

    @Override
    public boolean hasPriority() {
        return false;
    }
}
