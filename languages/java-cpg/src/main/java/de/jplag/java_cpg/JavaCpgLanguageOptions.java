package de.jplag.java_cpg;

import de.jplag.java_cpg.token.CpgTokenEquivalenceMode;
import de.jplag.java_cpg.token.characteristic.VectorComparisonMode;
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
            "The threshold for token equivalence with characteristic vectors. Only used if token-equivalence is set to CHARACTERISTIC. For cosine similarity the value is multiplied with 100 before being compared to the threshold",
            5);

    private final LanguageOption<Boolean> enableSemanticAnalysis = createDefaultOption(OptionType.bool(), "enable-semantic-analysis",
            "Whether to enable semantic token generation. If disabled, characteristic vectors will be calculated without data dependencies", false);

    private final LanguageOption<String> vectorComparisonStrategy = createDefaultOption(OptionType.string(), "vector-comparison-strategy",
            "The strategy to compare characteristic vectors. Possible values are EUCLIDEAN_DISTANCE, L1_DISTANCE and COSINE_SIMILARITY. Only used if token-equivalence is set to CHARACTERISTIC.",
            VectorComparisonMode.EUCLIDEAN_DISTANCE.name());

    /**
     * Getter for the token equivalence mode.
     * @return The token equivalence mode
     */
    public CpgTokenEquivalenceMode getTokenEquivalenceMode() {
        return CpgTokenEquivalenceMode.valueOf(this.tokenEquivalenceMode.getValue());
    }

    /**
     * Getter for the characteristic vector threshold.
     * @return The characteristic vector threshold
     */
    public int getCharacteristicVectorThreshold() {
        return this.characteristicVectorThreshold.getValue();
    }

    /**
     * Getter for whether semantic analysis is enabled.
     * @return True, if semantic analysis is enabled
     */
    public boolean isSemanticAnalysisEnabled() {
        return this.enableSemanticAnalysis.getValue();
    }

    /**
     * Getter for the vector comparison strategy.
     * @return The vector comparison strategy
     */
    public VectorComparisonMode getVectorComparisonStrategy() {
        return VectorComparisonMode.valueOf(this.vectorComparisonStrategy.getValue());
    }

    // setter for tests
    /**
     * Setter for the token equivalence mode.
     * @param mode The token equivalence mode
     */
    public void setTokenEquivalenceMode(CpgTokenEquivalenceMode mode) {
        this.tokenEquivalenceMode.setValue(mode.name());
    }

    // setter for tests

    /**
     * Setter for enabling semantic analysis.
     * @param enableSemanticAnalysis True, to enable semantic analysis
     */
    public void setEnableSemanticAnalysis(boolean enableSemanticAnalysis) {
        this.enableSemanticAnalysis.setValue(enableSemanticAnalysis);
    }

    // setter for tests
    /**
     * Setter for the characteristic vector threshold.
     * @param threshold The characteristic vector threshold
     */
    public void setCharacteristicVectorThreshold(int threshold) {
        this.characteristicVectorThreshold.setValue(threshold);
    }

    // setter for tests
    /**
     * Setter for the vector comparison strategy.
     * @param strategy The vector comparison strategy
     */
    public void setVectorComparisonStrategy(VectorComparisonMode strategy) {
        this.vectorComparisonStrategy.setValue(strategy.name());
    }
}
