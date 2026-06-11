package de.jplag.java.babylon;

import com.google.auto.service.AutoService;
import de.jplag.Language;
import de.jplag.java.JavaLanguage;
import de.jplag.java.Parser;

/**
 * {@link Language} implementation for Java with support for {@link jdk.incubator.code.CodeTransformer}-based transformations.
 * <br>
 * Does not use {@link AutoService} since the service is gated behind <a href="https://openjdk.org/jeps/238">MRJAR</a>.
 */
@AutoService(Language.class)
public class JavaBabylonLanguage extends JavaLanguage {
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
        return super.minimumTokenMatch();
    }

    @Override
    protected Parser createParser() {
        return new ParserBabylon(options.getTransformationPipeline(), options.getTokenizer());
    }

    @Override
    public BabylonOptions getOptions() {
        return options;
    }

    @Override
    public boolean supportsNormalization() {
        return false; // for now
    }

    @Override
    public boolean hasPriority() {
        return false;
    }
}
