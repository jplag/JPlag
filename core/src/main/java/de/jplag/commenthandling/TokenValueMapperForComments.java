package de.jplag.commenthandling;

import de.jplag.*;
import de.jplag.comparison.GreedyStringTiling;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps the tokens in a submission to integer IDs for usage in the {@link GreedyStringTiling} algorithm. Each token type
 * will be assigned a unique number. The token lists in that form can be queried by calling
 * {@link TokenValueMapperForComments#getTokenValuesFor(Submission)}.
 */
public class TokenValueMapperForComments {
    private final Map<TokenType, Integer> tokenTypeValues;
    private final Map<Submission, int[]> tokenValueMap;

    /**
     * Creates the submission to token ID mapping for a set of submissions. This will also show the progress to the user
     * using the {@link ProgressBarLogger}.
     * @param submissionSet is the set of submissions to process.
     */
    public TokenValueMapperForComments(SubmissionSet submissionSet) {
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
        List<Token> tokens = submission.getComments();
        int[] tokenValues = new int[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            TokenType type = tokens.get(i).getType();
            tokenTypeValues.putIfAbsent(type, tokenTypeValues.size());
            tokenValues[i] = tokenTypeValues.get(type);
        }
        this.tokenValueMap.put(submission, tokenValues);
    }

    /**
     * Queries the token IDs for a single submission. Each number in the array corresponds to one token from the submission.
     * The {@link SharedTokenType#FILE_END} token is guaranteed to be mapped to 0.
     * @param submission The submission to query.
     * @return the list of tokens.
     */
    public int[] getTokenValuesFor(Submission submission) {
        return this.tokenValueMap.get(submission);
    }
}
