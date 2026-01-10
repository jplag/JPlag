package de.jplag.java_cpg;

import de.jplag.java_cpg.token.CpgTokenEquivalenceMode;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

public class JavaCpgLanguageOptions extends LanguageOptions {

    private final LanguageOption<String> tokenEquivalenceMode = createDefaultOption(OptionType.string(), "token-equivalence",
            "The mode for token equivalence. Possible values are DEFAULT and SEMANTIC", CpgTokenEquivalenceMode.DEFAULT.name());

    private final LanguageOption<Integer> semanticThreshold = createDefaultOption(OptionType.integer(), "semantic-threshold",
            "The threshold for semantic token equivalence. Only used if token-equivalence is set to SEMANTIC. Range: 0-100", 5);

    private final LanguageOption<Boolean> enableSemanticAnalysis = createDefaultOption(OptionType.bool(), "enable-semantic-analysis",
            "Whether to enable semantic token generation. If disabled, semantic token equivalence mode cannot be used.", false);

    public CpgTokenEquivalenceMode getTokenEquivalenceMode() {
        return CpgTokenEquivalenceMode.valueOf(this.tokenEquivalenceMode.getValue());
    }

    public int getSemanticThreshold() {
        return this.semanticThreshold.getValue();
    }

    public boolean isSemanticAnalysisEnabled() {
        return this.enableSemanticAnalysis.getValue();
    }

    // setter for tests
    public void setTokenEquivalenceMode(CpgTokenEquivalenceMode mode) {
        this.tokenEquivalenceMode.setValue(mode.name());
    }

    // setter for tests
    public void setEnableSemanticAnalysis(boolean enable) {
        this.enableSemanticAnalysis.setValue(enable);
    }
}
