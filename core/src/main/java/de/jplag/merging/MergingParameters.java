package de.jplag.merging;

public class MergingParameters{
	private int mergeBuffer;
	private int seperatingThreshold;
	
	public MergingParameters(int mb, int st) {
		mergeBuffer=mb;
		seperatingThreshold=st;
	}
	
	public MergingParameters() {
		mergeBuffer=0;
		seperatingThreshold=0;
	}
	
	public int mergeBuffer() {
		return mergeBuffer;
	}
	
	public int seperatingThreshold() {
		return seperatingThreshold;
	}
}

