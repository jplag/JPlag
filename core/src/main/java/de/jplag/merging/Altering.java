package de.jplag.merging;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.options.JPlagOptions;
import de.jplag.Token;
import de.jplag.TokenType;
//import de.jplag.java.JavaTokenType;


public class Altering{
    private SubmissionSet submissionSet;
    private JPlagOptions options;
    private List<Submission> submissions;
    private Submission submission;
    private List<Token> tokenList;
    private Random rand;
    private String language;
    private List<TokenType> typeDict;
    
    public Altering(SubmissionSet s,JPlagOptions o) {
        submissionSet=s;
        submissions=submissionSet.getSubmissions();
        options=o;
        rand = new Random();
        language=options.language().getIdentifier();
    }
    
    public SubmissionSet run() {
        fillTypeDict();
        for(int i=0; i<submissionSet.numberOfSubmissions();i++) {
            submission=submissions.get(i);
            if(submission.getName().startsWith("a_")) {
                tokenList=new ArrayList<>(submission.getTokenList());
                randomAlteration();
                submission.setTokenList(tokenList); 
            }  
        }
        return submissionSet;
    }
    
    private void randomAlteration() {
        for(int i=0;i < tokenList.size();i++) {
            if (rand.nextInt(10) == 0) {
                if(language.equals("java")) {
                    tokenList.get(i).setType(typeDict.get(rand.nextInt(typeDict.size())));    
                }
                if(language.equals("cpp")) {
                    tokenList.get(i).setType(typeDict.get(rand.nextInt(typeDict.size())));    
                }
            }
        }
    }
    
    private void fillTypeDict() {
        typeDict = new ArrayList<>();
        for(int i=0; i<submissionSet.numberOfSubmissions();i++) {
            for(int j=0; j<submissions.get(i).getTokenList().size();j++) {
                if(!typeDict.contains(submissions.get(i).getTokenList().get(j).getType())) {
                    typeDict.add(submissions.get(i).getTokenList().get(j).getType());
                }
            }
        }
    }
}