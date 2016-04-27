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

  /* The bigger a match (length "anz") is relatively to the biggest match
   * the redder is the color returned by this method. */
  private final String color(int anz) {
    int farbe = 255*anz / biggestMatch();
    String help = (farbe<16 ? "0":"")+Integer.toHexString(farbe);
    return "#"+help+"0000";
  }

  /* This method generates an table entry in the list of all matches. */
  public final void HTMLreport(PrintWriter f, int matchnr, Program program) {
	Messages msg=program.msg;
    Match match;
    Token[] A = subA.struct.tokens;
    Token[] B = subB.struct.tokens;
    sort();

    f.println("<CENTER>\n<TABLE BORDER=\"1\" CELLSPACING=\"0\" "+
	      "BGCOLOR=\"#d0d0d0\">");
    f.println("<TR><TH><TH>"+subA.name+" ("+percentA()+"%)<TH>"+
	      subB.name+" ("+percentB()+"%)<TH>" + msg.getString("AllMatches.Tokens"));
    for (int i=0; i<size(); i++) {
      match = matches[i];
      //System.out.println("("+match.startA+","+match.startB+" - "+match.length+")");
      Token startA = A[match.startA];
      Token endeA = A[match.startA+match.length-1];
      Token startB = B[match.startB];
      Token endeB = B[match.startB+match.length-1];
      String col = Colors.getColor(i);
      f.print("<TR><TD BGCOLOR=\""+col+"\"><FONT COLOR=\""+col+"\">-</FONT>");
      f.print("<TD><A HREF=\"javascript:ZweiFrames('match"+
	      matchnr+"-0.html#"+i+"',2,'match"+
	      matchnr+"-1.html#"+i+"',3)\" NAME=\""+i+"\">");
      f.print(new String(startA.file.getBytes()));
      if (program.get_language().usesIndex())
    	  f.print("("+startA.getIndex()+"-"+endeA.getIndex()+")");
      else
    	  f.print("("+startA.getLine()+"-"+endeA.getLine()+")");
      f.print("<TD><A HREF=\"javascript:ZweiFrames('match"+
	      matchnr+"-0.html#"+i+"',2,'match"+
	      matchnr+"-1.html#"+i+"',3)\" NAME=\""+i+"\">");
      f.print(startB.file);
      if (program.get_language().usesIndex())
    	  f.print("("+startB.getIndex()+"-"+endeB.getIndex());
      else
    	  f.print("("+startB.getLine()+"-"+endeB.getLine());
      f.println(")</A><TD ALIGN=center>"+"<FONT COLOR=\""+
		color(match.length)+"\">"+match.length+"</FONT>");
    }
	if(program.useBasecode()){
			f.print("<TR><TD BGCOLOR=\"#C0C0C0\"><TD>"
				+ msg.getString("AllMatches.Basecode") + " "
				+ roundedPercentBasecodeA() + "%");
			f.println("<TD>" + msg.getString("AllMatches.Basecode") + " "
				+ roundedPercentBasecodeB() + "%<TD>&nbsp;");
	}
    f.println("</TABLE>\n</CENTER>");
  }

  /* This method generates an table which shows the distribution of match
   * lengths. */
  static private final int barLength = 70;
  public final void distributionReport(PrintWriter f, Messages msg) {
	  int[] dist;
	  int maxLength = 1;
	  int max = 0, tmp;

	  // find out the maximum length
	  for (int i=0; i<size(); i++)
		  if (matches[i].length > maxLength)
			  maxLength = matches[i].length;
	  // create the distribution
	  dist = new int[maxLength];
	  for (int i=0; i<maxLength; i++)
		  dist[i] = 0;
	  // fill out the distribution
	  for (int i=0; i<size(); i++) {
		  tmp = ++dist[matches[i].length-1];
		  if (tmp > max)
			  max = tmp;
	  }

	  f.println("<H4>" + msg.getString("AllMatches.Distribution")
		  + ":</H4>\n<CENTER>");
	  f.println("<TABLE CELLPADDING=1 CELLSPACING=1>");
	  f.println("<TR><TH ALIGN=center BGCOLOR=#8080ff>"
		  + msg.getString("AllMatches.Length")
		  + "<TH ALIGN=center BGCOLOR=#8080ff>"
		  + msg.getString("AllMatches.Number_of_matches")
		  + "<TH ALIGN=center BGCOLOR=#8080ff>.</TR>");
	  for (int i=0; i<maxLength; i++) {
		  if (dist[i] == 0) continue;
		  f.print("<TR><TD ALIGN=center BGCOLOR=#c0c0ff>" + (i+1)
			  + "<TD ALIGN=right BGCOLOR=#c0c0ff>" + dist[i]
			  + "<TD BGCOLOR=#c0c0ff>");
		  for (int j=(dist[i] * barLength / max); j>0; j--) f.print("#");
		  if (dist[i] * barLength / max == 0) {
			  if (dist[i]==0)
				  f.print(".");
			  else
				  f.print("#");
		  }
		  f.println("</TR>");
	  }
	  f.println("</TABLE></CENTER>");
  }

  /* This method is very inefficient but at the same time very useful for the
   * report.
   * It returns:
   * 0 : if the line "line" is not represented by a token.
   * 1 : if all the tokens from the line are covered by a single match.
   * 2 : if at least one token from the line does not belong to a match.
   */
  public final int diffType(String file, int line, int sub) {
	  Structure struct = (sub==0 ? subA:subB).struct;
	  int index = 0;
	  for (; index < struct.size(); index++)
		  if (struct.tokens[index].file.equals(file) &&
				  struct.tokens[index].getLine() == line) break;
	  if (index == struct.size()) return 0;
	  while (index < struct.size() && struct.tokens[index].getLine() == line &&
			  struct.tokens[index].file.equals(file)) {
		  int j = 0;
		  for (; j < size(); j++)
			  if (matches[j].contains(index, sub)) break;
		  if (j == size()) return 2;
		  index++;
	  }
	  return 1;
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
