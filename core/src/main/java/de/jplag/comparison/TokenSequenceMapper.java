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
 * Maps the tokens in a submission to integer IDs for usage in the {@link GreedyStringTiling} algorithm. Each token type
 * will be assigned a unique number. The token lists in that form can be queried by calling
 * {@link TokenSequenceMapper#getTokenSequenceFor(Submission)}.
 */
public class TokenSequenceMapper {
    private final Map<TokenType, Integer> tokenTypeToId;
    private final Map<Submission, int[]> submissionToTokenSequence;

    /**
     * Creates the submission to token ID mapping for a set of submissions. This will also show the progress to the user
     * using the {@link ProgressBarLogger}.
     * @param submissionSet is the set of submissions to process.
     */
    public TokenSequenceMapper(SubmissionSet submissionSet) {
        tokenTypeToId = new HashMap<>();
        submissionToTokenSequence = new IdentityHashMap<>();

        tokenTypeToId.put(SharedTokenType.FILE_END, 0);

        addSubmissions(submissionSet);
        if (submissionSet.hasBaseCode()) {
            addSingleSubmission(submissionSet.getBaseCode());
        }
    }

    private void addSubmissions(SubmissionSet submissionSet) {
        ProgressBarLogger.iterate(ProgressBarType.TOKEN_SEQUENCE_CREATION, submissionSet.getSubmissions(), this::addSingleSubmission);
    }

    private void addSingleSubmission(Submission submission) {
        List<Token> tokens = submission.getTokenList();
        int[] tokenSequence = new int[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            TokenType type = tokens.get(i).getType();
            tokenTypeToId.putIfAbsent(type, tokenTypeToId.size());
            tokenSequence[i] = tokenTypeToId.get(type);
        }
        submissionToTokenSequence.put(submission, tokenSequence);
    }

    /**
     * Queries the token IDs for a single submission. Each number in the array corresponds to one token from the submission.
     * The {@link SharedTokenType#FILE_END} token is guaranteed to be mapped to 0.
     * @param submission The submission to query.
     * @return the integer-based token-sequence.
     */
    public int[] getTokenSequenceFor(Submission submission) {
        return submissionToTokenSequence.get(submission);
    }
}
