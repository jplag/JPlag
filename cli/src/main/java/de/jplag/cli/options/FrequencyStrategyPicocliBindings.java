package de.jplag.cli.options;

import java.util.ArrayList;
import java.util.Map;

import de.jplag.frequency.CompleteMatchesStrategy;
import de.jplag.frequency.ContainedMatchesStrategy;
import de.jplag.frequency.FrequencyStrategy;
import de.jplag.frequency.SubMatchesStrategy;
import de.jplag.frequency.WindowOfMatchesStrategy;

import picocli.CommandLine;

public class FrequencyStrategyPicocliBindings extends ArrayList<String> implements CommandLine.ITypeConverter<FrequencyStrategy> {
    private final static Map<String, FrequencyStrategy> STRATEGIES = Map.of("complete", new CompleteMatchesStrategy(), "contained",
            new ContainedMatchesStrategy(), "subMatches", new SubMatchesStrategy(), "windowOfMatches", new WindowOfMatchesStrategy());

    public FrequencyStrategyPicocliBindings() {
        super(STRATEGIES.keySet());
    }

    @Override
    public FrequencyStrategy convert(String value) throws Exception {
        return STRATEGIES.get(value);
    }
}
