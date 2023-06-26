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
    private Boolean rules;
    
    public Altering(SubmissionSet s,JPlagOptions o) {
        submissionSet=s;
        submissions=submissionSet.getSubmissions();
        options=o;
        rand = new Random(1337);
        language=options.language().getIdentifier();
        rules=false;
    }
    
    public SubmissionSet run() {
        fillTypeDict();
        System.out.println(typeDict);
        for(int i=0; i<submissionSet.numberOfSubmissions();i++) {
            submission=submissions.get(i);
            if(submission.getName().startsWith("a_")) {
                tokenList=new ArrayList<>(submission.getTokenList());
                if(!rules) {
                    randomAlteration(); 
                }
                else {
                    if(language.equals("java")) {
                        applyJavaRules();
                    }
                    if(language.equals("cpp")) {
                        applyCPPRules();
                    }
                } 
                submission.setTokenList(tokenList); 
            }  
        }
        return submissionSet;
    }
    
    private void randomAlteration() {
        for(int i=0;i < tokenList.size();i++) {
            if (rand.nextInt(10) == 0) {
                tokenList.get(i).setType(typeDict.get(rand.nextInt(typeDict.size())));
                //tokenList.get(i).setType(ControlTokenTypes.J_APPLY);
            }
        }
    }
    
    private void applyJavaRules() {
        for(int i=0;i < tokenList.size();i++) {
            if(ControlTokenTypes.J_DO_BEGIN.equals(tokenList.get(i).getType())) {
                System.out.println("Token is J_DO_BEGIN");
            } 
        }  
    }
    
    private void applyCPPRules() {
        for(int i=0;i < tokenList.size();i++) {
            if(ControlTokenTypes.C_DO.equals(tokenList.get(i).getType())) {
                System.out.println("Token is C_DO");
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