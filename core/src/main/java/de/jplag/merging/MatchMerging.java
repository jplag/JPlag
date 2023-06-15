package de.jplag.merging;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.jplag.Match;
import de.jplag.Submission;
//import de.jplag.Token;

public class MatchMerging{
	private int minimumMatchLength;
	private int mergeBuffer;
	private Submission leftSubmission;
	private Submission rightSubmission;
	private List<Match> globalMatches;
	//private List<Token> leftTokens;
    //private List<Token> rightTokens;
    private List<List<Match>> neighbors;
    private int seperatingThreshold;
	
	public MatchMerging(int mml,int mb, Submission ls, Submission rs, List<Match> gm, int st){
		this.minimumMatchLength = mml;
		this.mergeBuffer = mb;
		this.leftSubmission = ls;
		this.rightSubmission = rs;
		this.globalMatches = gm;
		//this.leftTokens = this.leftSubmission.getTokenList();
		//this.rightTokens = this.rightSubmission.getTokenList();
		this.seperatingThreshold = st;
	}

	public int getMergeBuffer() {
		return mergeBuffer;
	}
	
	public void computeNeighbors() {
		neighbors= new ArrayList<>();
		List<Match> sortedByFirst= new ArrayList<>(globalMatches);
		Collections.sort(sortedByFirst, (m1, m2) -> m1.getStartOfFirst() - m2.getStartOfFirst());
		List<Match> sortedBySecond= new ArrayList<>(globalMatches);
		Collections.sort(sortedBySecond, (m1, m2) -> m1.getStartOfSecond() - m2.getStartOfSecond());
		for (int i = 0; i < sortedByFirst.size()-1; i++) {
			if(sortedBySecond.indexOf(sortedByFirst.get(i))==(sortedBySecond.indexOf(sortedByFirst.get(i+1))-1)) {
            	neighbors.add(Arrays.asList(sortedByFirst.get(i), sortedByFirst.get(i+1)));
			}
        }
		//System.out.println(neighbors);
	}
	
	public void mergeNeighbors() {
		int lengthThreshold = minimumMatchLength - mergeBuffer;
		int i=0;
		while (i < neighbors.size()) {
			double length = (neighbors.get(i).get(0).getLength() + neighbors.get(i).get(1).getLength())/2.0;
			double seperating =((neighbors.get(i).get(1).getStartOfFirst()-neighbors.get(i).get(0).endOfFirst()-1)+(neighbors.get(i).get(1).getStartOfSecond()-neighbors.get(i).get(0).endOfSecond()-1))/2.0;
			//Checking length is not necessary as GST already checked length while computing matches
			if(seperating <= seperatingThreshold) {
				System.out.println(length+" "+seperating);
				System.out.println("Original:"+ neighbors.get(i));
				globalMatches.removeAll(neighbors.get(i));
				System.out.println("Merged:"+ new Match(neighbors.get(i).get(0).getStartOfFirst(),neighbors.get(i).get(0).getStartOfSecond(),(int)(length*2+seperating)));
				globalMatches.add(new Match(neighbors.get(i).get(0).getStartOfFirst(),neighbors.get(i).get(0).getStartOfSecond(),(int)(length*2+seperating)));
				i=0;
				//Manuelles ändern wäre schneller
				computeNeighbors();
			}
			else {
				i++;
			}
		}
	}
	
	public void removeBuffer() {
		List<Match> toRemove = new ArrayList<Match>();
        for (Match m : globalMatches) {
            if(m.length()< minimumMatchLength+mergeBuffer) {
            	toRemove.add(m);
            }
        }
        globalMatches.removeAll(toRemove);
	}
	
	public Submission getLeftSubmission() {
		return leftSubmission;
	}
	
	public Submission getRightSubmission() {
		return rightSubmission;
	}

	public List<Match> getGlobalMatches() {
		return globalMatches;
	}	
}