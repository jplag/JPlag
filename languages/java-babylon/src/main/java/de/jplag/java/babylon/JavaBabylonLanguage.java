package de.jplag.java.babylon;

import com.google.auto.service.AutoService;
import de.jplag.Language;
import de.jplag.java.JavaLanguage;
import de.jplag.java.Parser;
import de.jplag.options.LanguageOptions;

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
        return new ParserBabylon(options.getTransformationPipeline());
    }

    @Override
    public BabylonOptions getOptions() {
        return options;
    }

    @Override
    public boolean supportsNormalization() {
        return false; // for now
    }
}
