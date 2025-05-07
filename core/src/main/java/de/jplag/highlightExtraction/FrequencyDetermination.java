package de.jplag.highlightExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.*;

public class FrequencyDetermination {

    private List<Pair<String, String>> myKeyValues = new ArrayList<>();
    private Map<String, List<String>> tokenFrequencyMap = new HashMap<>();

    public Map<String, List<String>> completeMatches(List<JPlagComparison> comparisons) {


        for (JPlagComparison myComparison : comparisons) {
            Submission left = myComparison.firstSubmission();

            List<Token> leftTokens = left.getTokenList();

            for (Match match : myComparison.matches()) {
                int start = match.startOfFirst();
                int len = match.length();

                if (start + len > leftTokens.size()) continue;

                List<String> myMatchTokens = new ArrayList<>();
                for (int i = start; i < start + len; i++) {
                    myMatchTokens.add(leftTokens.get(i).toString());
                }
                // Build Hashmap
                String myTokenKey = String.join(" ", myMatchTokens);

                String myComparisonIdentifier = myComparison.toString();

                tokenFrequencyMap.computeIfAbsent(myTokenKey, k -> new ArrayList<>()).add(myComparisonIdentifier);

                System.out.println(tokenFrequencyMap);

                myKeyValues.add(new Pair<>(myTokenKey, myComparisonIdentifier));

            }
        }
        return tokenFrequencyMap;
    }

    public Map<String, List<String>> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }

}
