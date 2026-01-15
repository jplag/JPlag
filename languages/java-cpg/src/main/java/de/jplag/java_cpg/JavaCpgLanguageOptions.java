package de.jplag.java_cpg;

import de.jplag.java_cpg.token.CpgTokenEquivalenceMode;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

/**
 * This class contains specific language options for the Java CPG language.
 */
public class JavaCpgLanguageOptions extends LanguageOptions {

    private final LanguageOption<String> tokenEquivalenceMode = createDefaultOption(OptionType.string(), "token-equivalence",
            "The mode for token equivalence. Possible values are DEFAULT and SEMANTIC", CpgTokenEquivalenceMode.DEFAULT.name());

    private final LanguageOption<Integer> characteristicVectorThreshold = createDefaultOption(OptionType.integer(), "characteristic-vector-threshold",
            "The threshold for token equivalence with characteristic vectors. Only used if token-equivalence is set to CHARACTERISTIC. Range: 0-100",
            5);

    private final LanguageOption<Boolean> enableSemanticAnalysis = createDefaultOption(OptionType.bool(), "enable-semantic-analysis",
            "Whether to enable semantic token generation. If disabled, characteristic vectors will be calculated without data dependencies", false);

    /**
     * Getter for the token equivalence mode
     * @return The token equivalence mode
     */
    public CpgTokenEquivalenceMode getTokenEquivalenceMode() {
        return CpgTokenEquivalenceMode.valueOf(this.tokenEquivalenceMode.getValue());
    }

    /**
     * Getter for the characteristic vector threshold
     * @return The characteristic vector threshold
     */
    public int getCharacteristicVectorThreshold() {
        return this.characteristicVectorThreshold.getValue();
    }

    /**
     * Getter for whether semantic analysis is enabled
     * @return True, if semantic analysis is enabled
     */
    public boolean isSemanticAnalysisEnabled() {
        return this.enableSemanticAnalysis.getValue();
    }

    // setter for tests
    /**
     * Setter for the token equivalence mode
     * @param mode The token equivalence mode
     */
    public void setTokenEquivalenceMode(CpgTokenEquivalenceMode mode) {
        this.tokenEquivalenceMode.setValue(mode.name());
    }

    // setter for tests

    /**
     * Setter for enabling semantic analysis
     * @param enableSemanticAnalysis True, to enable semantic analysis
     */
    public void setEnableSemanticAnalysis(boolean enableSemanticAnalysis) {
        this.enableSemanticAnalysis.setValue(enableSemanticAnalysis);
    }
}
