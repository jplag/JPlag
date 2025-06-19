package de.jplag.highlight_extraction;

import static de.jplag.highlight_extraction.StrategyMethods.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.jplag.Match;

/**
 * Strategy that uses submatches from the comparisons and calculates the frequency of their appearance in matches across
 * all submissions.
 */

public class ContainedStrategy implements FrequencyStrategy {
    int size;

    /**
     * Adds all submatches with min size length of the matches to a map using the token sequence as the key.
     * @param tokens Token list of the match.
     * @param comparisonId Identifier for the comparison.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param size Minimum length of the considered submatches.
     */
    // TODO liste von Tokentyps
    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        this.size = size;
        if (tokens.size() >= size) {
            for (int j = size; j <= tokens.size(); j++) {
                applyWindowCreate(map, tokens, j);
            }
            addToMap(map, createKey(tokens));
        }

    }

    /**
     * Calculates the frequency of all matches and adds them to the map.
     * @param tokens List of tokens representing the match.
     * @param comparisonId Identifier for the comparison.
     * @param map Map that associates token subsequences with how often they occur across comparisons.
     * @param size The minimum sub length considered in other strategies.
     */

    @Override
    public void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        if (tokens.size() >= size) {
            String key = createKey(tokens);
            List<String> idList = map.get(key);
            if (idList == null) {
                throw new IllegalStateException("Key not found in map: " + key);
            }
            idList.add(comparisonId);
        }

    }

    @Override
    public double calculateWeight(Match match, Map<String, List<String>> frequencyMap, List<String> matchToken) {
        List<String> keys = generateAllSubKeys(matchToken, size);
        List<Integer> frequencies = new ArrayList<>();
        for (String key : keys) {
            frequencies.add(frequencyMap.get(key).size());
        }
        // TODO all:
        // return frequencies.stream().mapToInt(Integer::intValue)
        // .average()
        // .orElse(0.0);

        return frequencies.stream().filter(freq -> freq > 0).mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
