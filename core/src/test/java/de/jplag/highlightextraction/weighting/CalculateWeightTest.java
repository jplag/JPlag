package de.jplag.highlightextraction.weighting;

import de.jplag.Match;
import de.jplag.TestBase;
import de.jplag.highlightextraction.MatchWeighting;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CalculateWeightTest extends TestBase {
    private Match testMatch(int len) {
        Match match = new Match(0, 0, len, len);
        match.setFrequencyWeight(0.0);
        return match;
    }

    private List<String> testTokenList(String... tokens) {
        return Arrays.asList(tokens);
    }

    private Map<String,List<String>> createFrequencyMap(String key) {
        Map<String,List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add("submission" + i);
        }
        map.put(key, list);
        return map;
    }

    @Test
    void testCompleteMatchesStrategyCalculateWeight( ) {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        List<String> tokens = testTokenList("a", "b", "c");
        String key = String.join(" ", tokens);
        Map<String,List<String>> map = createFrequencyMap(key);
        Match match = testMatch(3);

        // CalculateWeight sollte die Frequenz des kompletten Schlüssels zurückgeben (3)
        double weight = strategy.calculateWeight(match, map, tokens);
        assertEquals(3.0, weight, 0.001);
    }

    @Test
    void testContainedStrategyCalculateWeight( ) {
        ContainedStrategy strategy = new ContainedStrategy();
        List<String> tokens = testTokenList("a", "b", "c");

        Map<String,List<String>> frequencyMap = new HashMap<>();
        frequencyMap.put("a", List.of("1", "2"));      // freq = 2
        frequencyMap.put("a b", List.of("1"));         // freq = 1
        frequencyMap.put("a b c", List.of("1", "2", "3")); // freq = 3
        strategy.create(tokens, "s1", frequencyMap, 1);

        Match match = testMatch(3);

        double weight = strategy.calculateWeight(match, frequencyMap, tokens);
        double expected = (2 + 1 + 3) / 3.0;

        assertEquals(expected, weight, 0.001);
    }


    @Test
    void testSubMatchesStrategyCalculateWeight( ) {
        SubMatchesStrategy strategy = new SubMatchesStrategy();
        List<String> tokens = testTokenList("a", "b", "c", "d");

        Map<String,List<String>> frequencyMap = new HashMap<>();
        frequencyMap.put("a b", List.of("1", "2"));        // freq = 2
        frequencyMap.put("b c", List.of("1"));             // freq = 1
        frequencyMap.put("c d", List.of("1", "2", "3"));   // freq = 3
        frequencyMap.put("a b c", List.of("1", "2"));      // freq = 2
        frequencyMap.put("b c d", List.of("2"));           // freq = 1
        frequencyMap.put("a b c d", List.of("1"));         // freq = 1

        strategy.create(tokens, "s1", frequencyMap, 2);
        Match match = testMatch(4);
        double weight = strategy.calculateWeight(match, frequencyMap, tokens);
        double expected = (double) (2 + 1 + 3 + 2 + 1 + 1) /6;

        assertEquals(expected, weight, 0.001);
    }


    @Test
    void testWindowOfMatchesStrategyCalculateWeight( ) {
        WindowOfMatchesStrategy strategy = new WindowOfMatchesStrategy();
        List<String> tokens = testTokenList("x", "y", "z");
        Map<String,List<String>> frequencyMap = new HashMap<>();
        frequencyMap.put("x y", List.of("1", "2"));
        frequencyMap.put("y z", List.of("1", "2", "3"));

        WindowOfMatchesStrategy.size = 2;

        Match match = testMatch(3);
        double weight = strategy.calculateWeight(match, frequencyMap, tokens);
        double expected = (2 + 3) / 2.0;

        assertEquals(expected, weight, 0.001);
    }

    @Test
    void testMatchWeightingIntegrationWithStrategy( ) {
        List<String> tokens = testTokenList("a", "b", "c");

        Map<String,List<String>> frequencyMap = new HashMap<>();
        frequencyMap.put("a b c", List.of("s1", "s2")); // frequency == 2

        FrequencyStrategy strategy = new CompleteMatchesStrategy();

        Match match = testMatch(3);
        MatchWeighting weighting = new MatchWeighting(strategy, frequencyMap);
        weighting.weightMatch(match, tokens);

        assertEquals(2.0, match.getFrequencyWeight(), 0.01);
    }
}
