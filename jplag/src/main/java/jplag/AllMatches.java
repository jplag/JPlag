package jplag;

import java.util.Arrays;
import java.util.Comparator;
import java.io.*;

import jplag.options.util.Messages;

/* This class extends "Matches" to represent the whole result of a comparison.
 * Methods to ease the presentation of the result are added.
 */
public class AllMatches extends Matches implements Comparator<AllMatches> {
  public Submission subA;
  public Submission subB;

  public AllBasecodeMatches bcmatchesA = null;
  public AllBasecodeMatches bcmatchesB = null;


  public AllMatches(Submission subA, Submission subB) {
    super();
    this.subA = subA;
    this.subB = subB;
  }

  /* s==0 uses the start indexes of subA as key for the sorting algorithm.
   * Otherwise the start indexes of subB are used. */
  public final int[] sort_permutation(int s) {   // bubblesort!!!
    int size = size();
    int[] perm = new int[size];
    int i, j, tmp;

    // initialize permutation array
    for (i=0; i<size; i++)
      perm[i] = i;

    if (s==0) {     // submission A
      for (i=1; i<size; i++)
	for (j=0; j<(size-i); j++)
	  if (matches[perm[j]].startA > matches[perm[j+1]].startA) {
	    tmp = perm[j];
	    perm[j] = perm[j+1];
	    perm[j+1] = tmp;
	  }
    } else {        // submission B
      for (i=1; i<size; i++)
	for (j=0; j<(size-i); j++)
	  if (matches[perm[j]].startB > matches[perm[j+1]].startB) {
	    tmp = perm[j];
	    perm[j] = perm[j+1];
	    perm[j+1] = tmp;
	  }
    }
    return perm;
  }

  /* sort start indexes of subA
   */
  public final void sort() {   // bubblesort!!!
    Match tmp;
    int size = size();
    int i, j;

    for (i=1; i<size; i++)
      for (j=0; j<(size-i); j++)
	if (matches[j].startA > matches[j+1].startA) {
	  tmp = matches[j];
	  matches[j] = matches[j+1];
	  matches[j+1] = tmp;
	}
  }

  /* A few methods to calculate some statistical data
   */
  public final int tokensMatched() {
    int erg = 0;
    for (int i=0; i<size(); i++) erg += matches[i].length;
    return erg;
  }
  private final int biggestMatch() {
    int erg = 0;
    for (int i=0; i<size(); i++)
      if (matches[i].length>erg) erg=matches[i].length;
    return erg;
  }
  public final boolean moreThan(float percent) {
    return (percent() > percent);
  }
  public final float roundedPercent() {
    float percent = percent();
    return ((int)(percent * 10)) / (float)10;
  }
  public final float percent() {
	float sa, sb;
	if(bcmatchesB != null && bcmatchesA != null){
		sa = subA.size() - subA.files.length - bcmatchesA.tokensMatched();
    	sb = subB.size() - subB.files.length - bcmatchesB.tokensMatched();
	}
	else{
		sa = subA.size() - subA.files.length;
		sb = subB.size() - subB.files.length;
	}
    return (200*(float)tokensMatched())/(sa+sb);
  }
  public final float percentA() {
  	int divisor;
  	if(bcmatchesA != null) divisor = subA.size()-subA.files.length-bcmatchesA.tokensMatched();
    else divisor = subA.size()-subA.files.length;
    return (divisor == 0 ? 0f : (tokensMatched()*100 / (float) divisor));
  }
  public final float percentB() {
    int divisor;
	if(bcmatchesB != null) divisor = subB.size()-subB.files.length-bcmatchesB.tokensMatched();
	else divisor = subB.size()-subB.files.length;
    return (divisor == 0 ? 0f : (tokensMatched()*100 / (float) divisor));
  }

  public final float roundedPercentMaxAB() {
      float percent = percentMaxAB();
      return ((int)(percent * 10)) / (float)10;
  }
  public final float percentMaxAB() {
      float a=percentA();
      float b=percentB();
      if(a>b) return a;
      else return b;
  }

  public final float roundedPercentMinAB() {
      float percent = percentMinAB();
      return ((int)(percent * 10)) / (float)10;
  }
  public final float percentMinAB() {
      float a=percentA();
      float b=percentB();
      if(a<b) return a;
      else return b;
  }

  public final float percentBasecodeA(){
	float sa = subA.size() - subA.files.length;
	return bcmatchesA.tokensMatched() * 100 / sa;
  }
  public final float percentBasecodeB(){
	float sb = subB.size() - subB.files.length;
	return bcmatchesB.tokensMatched() * 100 / sb;
  }
  public final float roundedPercentBasecodeA() {
	float percent = percentBasecodeA();
	return ((int)(percent * 10)) / (float)10;
  }
  public final float roundedPercentBasecodeB() {
	float percent = percentBasecodeB();
	return ((int)(percent * 10)) / (float)10;
  }

  /* Returns the name of the submissions which were compared
   * Parameter: i == 0   submission A,
   *            i != 0   submission B.
   */
  public final String subName(int i) {
    return (i==0 ? subA.name : subB.name);
  }

  /* This method returns all the files which contributed to a match.
   * Parameter: j == 0   submission A,
   *            j != 0   submission B.
   */
  public final String[] files(int j) {
	  Token[] tokens = (j==0 ? subA : subB).struct.tokens;
	  int i,h,starti,starth,count = 1;
o1:   for (i=1; i<size(); i++) {
		  starti = (j==0 ? matches[i].startA : matches[i].startB);
		  for (h=0; h<i; h++) {
			  starth = (j==0 ? matches[h].startA : matches[h].startB);
			  if (tokens[starti].file.equals(tokens[starth].file)) continue o1;
		  }
		  count++;
	  }
	  String[] res = new String[count];
	  res[0] = tokens[(j==0 ? matches[0].startA : matches[0].startB)].file;
	  count = 1;
o2:   for (i=1; i<size(); i++) {
		  starti = (j==0 ? matches[i].startA : matches[i].startB);
		  for (h=0; h<i; h++) {
			  starth = (j==0 ? matches[h].startA : matches[h].startB);
			  if (tokens[starti].file.equals(tokens[starth].file)) continue o2;
		  }
		  res[count++] = tokens[starti].file;
	  }

	  /* sort by file name. (so that equally named files are displayed
	   * approximately side by side.) */
	  Arrays.sort(res);

	  return res;
  }

  /* This method returns the name of all files that are represented by
   * at least one token. */
  public final String[] allFiles(int sub) {
	  Structure struct = (sub==0 ? subA : subB).struct;
	  int count = 1;
	  for (int i=1; i<struct.size(); i++)
		  if (!struct.tokens[i].file.equals(struct.tokens[i-1].file))
			  count++;
	  String[] res = new String[count];
	  if (count > 0) res[0] = struct.tokens[0].file;
	  count = 1;
	  for (int i=1; i<struct.size(); i++)
		  if (!struct.tokens[i].file.equals(struct.tokens[i-1].file))
			  res[count++] = struct.tokens[i].file;

	  /* bubblesort by file name. (so that equally named files are displayed
	   * approximately side by side.) */
	  for (int a=1; a<res.length; a++)
		  for (int b=1; b<(res.length-a); b++)
			  if (res[b-1].compareTo(res[b])<0) {
				  String hilf = res[b-1]; res[b-1] = res[b]; res[b] = hilf;
			  }

	  return res;
  }

  public int compare(AllMatches o1, AllMatches o2) {
    float p1 = o1.percent();
    float p2 = o2.percent();
    if (p1 == p2) return 0;
    if (p1 > p2)
      return -1;
    else
      return 1;
  }

  public boolean equals(Object obj) {
	if(!(obj instanceof AllMatches)) return false;
    return (compare(this, (AllMatches) obj) == 0);
  }

  public String toString() {
	  return subA.name + " <-> " + subB.name;
  }

  public static class AvgComparator implements Comparator<AllMatches> {
      public int compare(AllMatches o1, AllMatches o2) {
          float p1 = o1.percent();
          float p2 = o2.percent();
          if (p1 == p2) return 0;
          if (p1 > p2)
              return -1;
          else
              return 1;
      }
  }

  public static class AvgReversedComparator implements Comparator<AllMatches> {
      public int compare(AllMatches o1, AllMatches o2) {
          float p1 = o1.percent();
          float p2 = o2.percent();
          if (p1 == p2) return 0;
          if (p1 < p2)
              return -1;
          else
              return 1;
      }
  }

  public static class MaxComparator implements Comparator<AllMatches> {
      public int compare(AllMatches o1, AllMatches o2) {
          float p1 = o1.percentMaxAB();
          float p2 = o2.percentMaxAB();
          if (p1 == p2) return 0;
          if (p1 > p2)
              return -1;
          else
              return 1;
      }
  }

  public static class MaxReversedComparator implements Comparator<AllMatches> {
      public int compare(AllMatches o1, AllMatches o2) {
          float p1 = o1.percentMaxAB();
          float p2 = o2.percentMaxAB();
          if (p1 == p2) return 0;
          if (p1 < p2)
              return -1;
          else
              return 1;
      }
  }

  public static class MinComparator implements Comparator<AllMatches> {
      public int compare(AllMatches o1, AllMatches o2) {
          float p1 = o1.percentMinAB();
          float p2 = o2.percentMinAB();
          if (p1 == p2) return 0;
          if (p1 > p2)
              return -1;
          else
              return 1;
      }
  }

  public static class MinReversedComparator implements Comparator<AllMatches> {
      public int compare(AllMatches o1, AllMatches o2) {
          float p1 = o1.percentMinAB();
          float p2 = o2.percentMinAB();
          if (p1 == p2) return 0;
          if (p1 < p2)
              return -1;
          else
              return 1;
      }
  }
}
