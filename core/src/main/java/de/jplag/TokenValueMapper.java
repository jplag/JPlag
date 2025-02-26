package de.jplag;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;

public class TokenValueMapper {
    private final Map<TokenType, Integer> tokenTypeValues;
    private final Map<Submission, int[]> tokenValueMap;

    public TokenValueMapper(SubmissionSet submissionSet) {
        this.tokenTypeValues = new HashMap<>();
        this.tokenValueMap = new IdentityHashMap<>();

        this.tokenTypeValues.put(SharedTokenType.FILE_END, 0);

        addSubmissions(submissionSet);
        if (submissionSet.hasBaseCode()) {
            addSingleSubmission(submissionSet.getBaseCode());
        }
    }

    private void addSubmissions(SubmissionSet submissionSet) {
        ProgressBarLogger.iterate(ProgressBarType.HASH_CREATION, submissionSet.getSubmissions(), this::addSingleSubmission);
    }

    private void addSingleSubmission(Submission submission) {
        List<Token> tokens = submission.getTokenList();
        int[] tokenValues = new int[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            TokenType type = tokens.get(i).getType();
            tokenTypeValues.putIfAbsent(type, tokenTypeValues.size());
            tokenValues[i] = tokenTypeValues.get(type);
        }
        this.tokenValueMap.put(submission, tokenValues);
    }

    public int[] getTokenValuesFor(Submission submission) {
        return this.tokenValueMap.get(submission);
    }
}
