package de.jplag.java.babylon;

import de.jplag.java.JavaLanguage;
import de.jplag.java.Parser;

public class JavaBabylonLanguage extends JavaLanguage {
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
        return new ParserBabylon();
    }

    @Override
    public boolean supportsNormalization() {
        return false; // for now
    }
}
