package de.jplag.merging;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.options.JPlagOptions;
import de.jplag.Token;
import de.jplag.TokenType;


public class Altering{
    private SubmissionSet submissionSet;
    private JPlagOptions options;
    private List<Submission> submissions;
    private Submission submission;
    private List<Token> tokenList;
    private Random rand;
    private List<TokenType> typeDict;

    
    public Altering(SubmissionSet s,JPlagOptions o) {
        submissionSet=s;
        submissions=submissionSet.getSubmissions();
        options=o;
        rand = new Random(1337);
    }
    
    public void run() {
        fillTypeDict();
        System.out.println(typeDict);
        for(int i=0; i<submissionSet.numberOfSubmissions();i++) {
            submission=submissions.get(i);
            if(submission.getName().startsWith("a_")) {
                tokenList=new ArrayList<>(submission.getTokenList());
                randomAlteration(); 
                submission.setTokenList(tokenList); 
            }  
        }
    }
    
    private void randomAlteration() {
        //Ignore FILE_END
        for(int i=0;i < tokenList.size()-1;i++) {
            //20% Chance of Alteration
            if (rand.nextInt(10) <= 1) {
                tokenList.get(i).setType(typeDict.get(rand.nextInt(typeDict.size())));
            }
        }
    }
    
    private void fillTypeDict() {
        typeDict = new ArrayList<>();
        for(int i=0; i<submissionSet.numberOfSubmissions();i++) {
            //Ignore FILE_END
            for(int j=0; j<submissions.get(i).getTokenList().size()-1;j++) {
                if(!typeDict.contains(submissions.get(i).getTokenList().get(j).getType())) {
                    typeDict.add(submissions.get(i).getTokenList().get(j).getType());
                }
            }
        }
    }
}