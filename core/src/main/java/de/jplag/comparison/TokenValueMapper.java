package de.jplag.comparison;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import de.jplag.SharedTokenType;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;

/**
 * Maps the tokens in a submission names to integers so they can be used by {@link GreedyStringTiling}. Each token type
 * will be assigned a unique number. The token lists in that form can be queried by calling
 * {@link TokenValueMapper#getTokenValuesFor(Submission)}.
 */
public class TokenValueMapper {
    private final Map<TokenType, Integer> tokenTypeValues;
    private final Map<Submission, int[]> tokenValueMap;

    /**
     * Crates the mapping for a single submission. This will also show the progress to the user using the
     * {@link ProgressBarLogger}.
     * @param submissionSet The set of submissions to process.
     */
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
        ProgressBarLogger.iterate(ProgressBarType.TOKEN_VALUE_CREATION, submissionSet.getSubmissions(), this::addSingleSubmission);
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

    /**
     * Queries the token list for a single submission. each number in the array corresponds to one token from the
     * submissions. The {@link SharedTokenType#FILE_END} token is guaranteed to be mapped to 0.
     * @param submission The submission to query.
     * @return the list of tokens.
     */
    public int[] getTokenValuesFor(Submission submission) {
        return this.tokenValueMap.get(submission);
    }
}
