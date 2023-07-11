package de.jplag.merging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.options.JPlagOptions;

public class Altering {
    private SubmissionSet submissionSet;
    private JPlagOptions options;
    private List<Submission> submissions;
    private Submission submission;
    private List<Token> tokenList;
    private Random rand;
    private List<TokenType> typeDict;
    private int percent;

    public Altering(SubmissionSet s, JPlagOptions o) {
        submissionSet = s;
        submissions = submissionSet.getSubmissions();
        options = o;
        if (options.alteringParameters().seed() == 0) {
            rand = new Random();
        } else {
            rand = new Random(options.alteringParameters().seed());
        }
        percent = options.alteringParameters().percent();
    }

    public void run() {
        if (percent == -1) {
            return;
        }
        fillTypeDict();
        System.out.println(typeDict);
        for (int i = 0; i < submissionSet.numberOfSubmissions(); i++) {
            submission = submissions.get(i);
            tokenList = new ArrayList<>(submission.getTokenList());
            if (submission.getName().startsWith("s_")) {
                randomPairwiseSwapping();
            }
            if (submission.getName().startsWith("a_")) {
                randomAlteration();
            }
            submission.setTokenList(tokenList);
        }
    }

    private void randomPairwiseSwapping() {
        // Ignore FILE_END
        for (int i = 0; i < tokenList.size() - 2; i++) {
            if (rand.nextInt(10) <= percent) {
                TokenType upper = tokenList.get(i).getType();
                TokenType lower = tokenList.get(i + 1).getType();
                tokenList.get(i).setType(lower);
                tokenList.get(i + 1).setType(upper);
            }
        }
    }

    private void randomAlteration() {
        // Ignore FILE_END
        for (int i = 0; i < tokenList.size() - 1; i++) {
            if (rand.nextInt(10) <= percent) {
                tokenList.get(i).setType(typeDict.get(rand.nextInt(typeDict.size())));
            }
        }
    }

    private void fillTypeDict() {
        typeDict = new ArrayList<>();
        for (int i = 0; i < submissionSet.numberOfSubmissions(); i++) {
            // Ignore FILE_END
            for (int j = 0; j < submissions.get(i).getTokenList().size() - 1; j++) {
                if (!typeDict.contains(submissions.get(i).getTokenList().get(j).getType())) {
                    typeDict.add(submissions.get(i).getTokenList().get(j).getType());
                }
            }
        }
    }
}