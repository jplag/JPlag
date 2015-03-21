package jplag.clustering;

import jplag.*;

import java.io.File;
import java.util.*;

public class ThemeGenerator {
	/*
	 * Changed By Emeric :
	 * This is a class that provide 4 procedures which are used by Clusters
	 * I have extended the procedure with parameter Program
	 */
	public static  void loadStructure(Submission sub) {
		sub.struct = new Structure();
		sub.struct.load(new File("temp", sub.dir.getName() + sub.name));
	}
	
	static public String generateThemes(Set<Submission> submissions, int[] themewords,
			boolean generateHTML,Program program) {
		int thLength = themewords.length;
		int[][][] results = new int[thLength][][];
		
		for (int i=0; i<thLength; i++)
			results[i] = (i != 0)
					? generateTheme(submissions, themewords[i], i+1,program)
					: generateTheme(submissions, themewords[i],program);
		
		String[][][] words = new String[thLength][][];
		for (int i=0; i<thLength; i++) {
			if (results[i] != null) {
				words[i] = new String[results[i].length/2][];
				for (int j=0; (j<(results[i].length/2)); j++)
					words[i][j] = new String[i+1];
			}
		}
		
		/* old!  new one below!
		 // now get the actual strings out of the hashtable (which is not so easy)
		  // -> very inefficient ! (iterates through all the words in the hashtable)
		   for (Iterator i=jplag.text.TextToken.entrySet().iterator();
		   i.hasNext();) {
		   Map.Entry entry = (Map.Entry)i.next();
		   int type = ((Integer)entry.getValue()).intValue();
		   for (int j=0; j<thLength; j++)
		   if (results[j] != null)
		   for (int k=0; k<results[j].length; k+=2)
		   for (int l=0; l<=j; l++)
		   if (results[j][k][l] == type)
		   words[j][k/2][l] = (String)entry.getKey();
		   }
		   */
		
		Language language = program.get_language();
		for (int j=0; j<thLength; j++)
			if (results[j] != null)
				for (int k=0; k<results[j].length; k+=2)
					for (int l=0; l<=j; l++)
						words[j][k/2][l] = language.type2string(results[j][k][l]).trim();
		
		String result = "";
		
		for (int i=0; i<thLength; i++) {
			if (results[i] != null)
				for (int j=0; j<results[i].length; j+=2) {
					if (generateHTML)
						result += "\"<B>";
					for (int k=0; k<=i; k++)
						result += words[i][j/2][k] + ((k!=i) ? " " : "");
					if (generateHTML)
						result += "</B>\"";
					if ((j+2 == results[i].length) ||
							(results[i][j+1][0] != results[i][j+3][0]))
						result += " ("+results[i][j+1][0]+")  ";
					else
						result += ", ";
				}
		}
		
		return result;
	}
	
	/* result setup: {{word_nr_1_1, word_nr_1_2, ...}, {frequency_1},
	 *                {word_nr_2_1, word_nr_2_2, ...}, {frequency_2}, ...}
	 */
	static public int[][] generateTheme(Set<Submission> submissions, int themewords,
			Program program) {
		if (themewords == 0)
			return null;
		
		int noOfWords =program.get_language().noOfTokens();
		int[] tokenFrequency = new int[noOfWords + 1];
		
		// initialize
		for (int i=0; i<noOfWords; i++)
			tokenFrequency[i] = 0;
		
		// count frequency
		for (Iterator<Submission> i = submissions.iterator(); i.hasNext(); ) {
			Submission submission = i.next();
			if (program.use_externalSearch()) {
				loadStructure(submission);
			}
			Token[] tokens = submission.struct.tokens;
			for (int j=(submission.size()-1); j>=0; j--) {
				if (tokens[j].type != TokenConstants.FILE_END)
					tokenFrequency[tokens[j].type]++;
			}
			if (program.use_externalSearch()) {
				submission.struct = null;
			}
		}
		
		// Data structures for the "Top-Ten"
		int[] number = new int[themewords+1];
		int[] index = new int[themewords+1];
		for (int i=0; i<=themewords; i++) {
			index[i] = -1;
			number[i] = 0;
		}
		
		// Create the "Top-Ten"
		for (int i=0; i<noOfWords; i++) {
			int num = tokenFrequency[i];
			// insert at bottom if possible
			if (number[themewords] < num) {
				number[themewords] = num;
				index[themewords] = i;
			} else continue;
			// re-sort if necessary -> inner bubble-sort
			for (int j=themewords; j>0; j--) {
				if (number[j-1] < number[j]) { // exchange
					int tmpNum = number[j-1];
					int tmpInd = index[j-1];
					number[j-1] = number[j];
					index[j-1] = index[j];
					number[j] = tmpNum;
					index[j] = tmpInd;
				} else break;
			}
		}
		
		int actual_nr = themewords;
		// cut of the tail - if there is one.
		if (number[themewords] == number[themewords-1]) {
			int cutoffNumber = number[themewords];
			int i;
			for (i=themewords; (i>=0 && number[i]==cutoffNumber); i--) {
				index[i] = -1;
				number[i] = 0;
			}
			actual_nr = i+1;
		}
		
		// result setup: {{word_nr_1_1, word_nr_1_2, ...}, {frequency_1},
		//                {word_nr_2_1, word_nr_2_2, ...}, {frequency_2}, ...}
		int[][] result = new int[actual_nr * 2][];
		for (int i=0; i<actual_nr; i++) {
			result[2*i] = new int[1];
			result[2*i][0] = index[i];
			result[2*i+1] = new int[1];
			result[2*i+1][0] = number[i];
		}
		
		return result;
	}
	
	/* result setup: {{word_nr_1_1, word_nr_1_2, ...}, {frequency_1},
	 *                {word_nr_2_1, word_nr_2_2, ...}, {frequency_2}, ...}
	 */
	static public int[][] generateTheme(Set<Submission> submissions, int themewords,
			int length, Program program) {
		if (themewords == 0)
			return null;
		
		// initialize
		int hashtableSize = 0;
		for (Iterator<Submission> i=submissions.iterator(); i.hasNext();) {
			Submission submission = i.next();
			hashtableSize += submission.size();
		}
		
		Hashtable<IntArray, IntValue> table =
            new Hashtable<IntArray, IntValue>((int)(0.75*hashtableSize));
		
		// count frequency
		for (Iterator<Submission> i=submissions.iterator(); i.hasNext();) {
			Submission submission = i.next();
			if (program.use_externalSearch()) {
				ThemeGenerator.loadStructure(submission);
			}
			Token[] tokens = submission.struct.tokens;
			int size = submission.size();
out:  		for (int j=0; j<=size-length; j++) {
				if (tokens[j+length-1].type == TokenConstants.FILE_END) {
					j += length-1;
					continue out;
				}
				IntArray subArray = new IntArray(length);
				for (int k=0; k<length; k++) {
					subArray.setField(k, tokens[j+k].type);
					if (tokens[j+k].type == TokenConstants.FILE_END)
						continue out;
				}
				Object value = table.get(subArray);
				if (value == null) {
					table.put(subArray, new IntValue(1));
				} else {
					((IntValue)value).inc();
				}
			}
			if (program.use_externalSearch()) {
				submission.struct = null;
			}
		}
		
		// Data structures for the "Top-Ten"
		int[] number = new int[themewords+1];
		int[][] index = new int[themewords+1][];
		for (int i=0; i<=themewords; i++) {
			number[i] = 0;
			index[i] = null;
		}
		
		// Create the "Top-Ten"
		for (Enumeration<IntArray> e=table.keys(); e.hasMoreElements();) {
			IntArray subArray = e.nextElement();
			int num = table.get(subArray).getValue();
			// insert at bottom if possible
			if (number[themewords] < num) {
				number[themewords] = num;
				index[themewords] = subArray.getFields();
			} else continue;
			// re-sort if necessary -> inner bubble-sort
			for (int j=themewords; j>0; j--) {
				if (number[j-1] < number[j]) { // exchange
					int tmpNum = number[j-1];
					int[] tmpInd = index[j-1];
					number[j-1] = number[j];
					index[j-1] = index[j];
					number[j] = tmpNum;
					index[j] = tmpInd;
				} else break;
			}
		}
		
		int actual_nr = themewords;
		// cut of the tail - if there is one.
		if (number[themewords] == number[themewords-1]) {
			int cutoffNumber = number[themewords];
			int i;
			for (i=themewords; (i>=0 && number[i]==cutoffNumber); i--) {
				index[i] = null;
				number[i] = 0;
			}
			actual_nr = i+1;
		}
		
		// result setup: {{word_nr_1_1, word_nr_1_2, ...}, {frequency_1},
		//                {word_nr_2_1, word_nr_2_2, ...}, {frequency_2}, ...}
		int[][] result = new int[actual_nr * 2][];
		for (int i=0; i<actual_nr; i++) {
			result[2*i] = new int[1];
			result[2*i] = index[i];
			result[2*i+1] = new int[1];
			result[2*i+1][0] = number[i];
		}
		
		return result;
	}
}


class IntArray {
	int[] array;
	
	public IntArray(int length) {
		array = new int[length];
		for (int i=0; i<length; i++)
			array[i] = -1;
	}
	
	public void setField(int index, int value) {
		array[index] = value;
	}
	
	public int[] getFields() {
		return array;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof IntArray))
			return false;
		int[] otherArray = ((IntArray)obj).array;
		int length = array.length;
		if (otherArray.length != length)
			return false;
		for (int i=0; i<length; i++)
			if (array[i] != otherArray[i])
				return false;
		return true;
	}
	
	public int hashCode() {
		int hash = array[0];
		for (int i=1; i<array.length; i++)
			hash = (hash * 16) + array[i];
		return hash;
	}
}

class IntValue {
	int value;
	
	public IntValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void inc() {
		value++;
	}
}
