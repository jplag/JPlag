package jplag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import jplag.clustering.Cluster;
import jplag.clustering.ThemeGenerator;
import jplag.options.Options;
import jplag.options.util.Messages;
import jplag.options.util.TagParser;

/**
 * This class writes all the HTML pages
 */
public class Report implements TokenConstants {
	private Program program;
	private Messages msg;
	private File root;
	private int[] dist;
	private Options options;
	SortedVector<AllMatches> avgmatches;
	SortedVector<AllMatches> maxmatches;
	SortedVector<AllMatches> minmatches;
	SortedVector<AllMatches> bcmatches;

	Map<AllMatches, Integer> matchesIndexMap = new HashMap<AllMatches, Integer>();
	int curMatchIndex = 0;

	// how much did we save?
	private Language language;

	public Report(Program program, Language language) {
		this.program = program;
		this.language = language;
		this.msg = program.msg;
	}

	public void write(File f, int[] dist, SortedVector<AllMatches> avgmatches, SortedVector<AllMatches> maxmatches,
			SortedVector<AllMatches> minmatches, Cluster clustering, Options options) throws jplag.ExitException {
		root = f;
		this.dist = dist;
		this.avgmatches = avgmatches;
		this.maxmatches = maxmatches;
		this.minmatches = minmatches;
		this.options = options;

		System.gc();

		writeIndex((clustering != null));

		if (this.program.use_clustering())
			writeClusters(clustering);

		copyFixedFiles(f);
		writeMatches(avgmatches);
		if (maxmatches != null)
			writeMatches(maxmatches);
		if (minmatches != null)
			writeMatches(minmatches);
	}

	// open file
	public HTMLFile openHTMLFile(File root, String name) throws ExitException {
		if (!root.exists())
			if (!root.mkdirs()) {
				throw new jplag.ExitException("Cannot create directory!");
			}
		if (!root.isDirectory()) {
			throw new jplag.ExitException(root + " is not a directory!");
		}
		if (!root.canWrite()) {
			throw new jplag.ExitException("Cannot write directory: " + root);
		}
		// now the actual file creation:    
		File f = new File(root, name);
		HTMLFile res = null;
		try {
			res = HTMLFile.createHTMLFile(f);
		} catch (IOException e) {
			throw new jplag.ExitException("Error opening file: " + f.toString());
		}
		return res;
	}

	public void writeHTMLHeader(HTMLFile file, String title) {
		file.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		file.println("<HTML><HEAD><TITLE>" + title + "</TITLE>");
		file.println("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		file.println("</HEAD>");
	}

	public void writeHTMLHeaderWithScript(HTMLFile file, String title) {
		file.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		file.println("<HTML>\n<HEAD>\n <TITLE>" + title + "</TITLE>");
		file.println("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		file.println("  <script type=\"text/javascript\">\n  <!--");
		file.println("   function ZweiFrames(URL1,F1,URL2,F2)\n   {");
		file.println("    parent.frames[F1].location.href=URL1;");
		file.println("    parent.frames[F2].location.href=URL2;\n   }\n  //-->");
		file.println("  </script>\n</HEAD>");
	}

	//  INDEX
	private final int bar_length = 75;

	private void writeDistribution(HTMLFile f) {
		// Die Verteilung:
		int max = 0;
		for (int i = 0; i < 10; i++)
			if (dist[i] > max)
				max = dist[i];
		f.println("<H4>" + this.msg.getString("Report.Distribution") + ":</H4>\n<CENTER>");
		f.println("<TABLE CELLPADDING=1 CELLSPACING=1>");
		for (int i = 9; i >= 0; i--) {
			f.print("<TR BGCOLOR=" + color(i * 10 + 10, 128, 192, 128, 192, 255, 255) + "><TD ALIGN=center>" + (i * 10) + "% - "
					+ (i * 10 + 10) + "%" + "</TD><TD ALIGN=right>" + dist[i] + "</TD><TD>");
			for (int j = (dist[i] * bar_length / max); j > 0; j--)
				f.print("#");
			if (dist[i] * bar_length / max == 0) {
				if (dist[i] == 0)
					f.print(".");
				else
					f.print("#");
			}
			f.println("</TD></TR>");
		}
		f.println("</TABLE></CENTER>\n<P>\n<HR>");
	}

	private int getMatchIndex(AllMatches match) {
		Object obj = matchesIndexMap.get(match);
		if (obj == null) {
			matchesIndexMap.put(match, new Integer(curMatchIndex++));
			return curMatchIndex - 1;
		} else
			return ((Integer) obj).intValue();
	}

	abstract class MatchesHelper {
		public abstract float getPercent(AllMatches matches);
	}

	private void writeLinksToMatches(HTMLFile f, SortedVector<AllMatches> matches, MatchesHelper helper, String headerStr, String csvfile) {
		//		output all the matches
		//		Set<String> namesPrinted = new Set();
		Set<AllMatches> matchesPrinted = new HashSet<AllMatches>();

		f.println(headerStr + " (<a href=\"help-sim-" + program.getCountryTag() + ".html\"><small><font color=\"#000088\">"
				+ msg.getString("Report.WhatIsThis") + "</font></small></a>):</H4>");
		f.println("<p><a href=\"" + csvfile + "\">download csv</a></p>");
		f.println("<TABLE CELLPADDING=3 CELLSPACING=2>");

		int anz = matches.size();
		for (int i = 0; ((i < anz) && (matchesPrinted.size() != anz)); i++) {
			AllMatches match = matches.elementAt(i);
			if (!matchesPrinted.contains(match)) {
				//				!namesPrinted.contains(match.subName(j))) {
				int a = 0, b = 0;
				String nameA = match.subName(0);
				String nameB = match.subName(1);
				//				Which of both submissions is referenced more often in "matches"?
				for (int x = 0; x < anz; x++) {
					AllMatches tmp = matches.elementAt(x);
					if (tmp != match && !matchesPrinted.contains(tmp)) {
						String tmpA = tmp.subName(0);
						String tmpB = tmp.subName(1);
						if (nameA.equals(tmpA) || nameA.equals(tmpB))
							a += helper.getPercent(tmp);
						if (nameB.equals(tmpA) || nameB.equals(tmpB))
							b += helper.getPercent(tmp);
					}
				}
				String name = (a >= b ? nameA : nameB);
				boolean header = false;
				//				namesPrinted.put(name);

				AllMatches output;
				for (int x = 0; x < anz; x++) {
					output = matches.elementAt(x);
					if (!matchesPrinted.contains(output) && (output.subName(0).equals(name) || output.subName(1).equals(name))) {
						matchesPrinted.add(output);
						int other = (output.subName(0).equals(name) ? 1 : 0);
						if (!header) { // only print header when necessary!
							header = true;
							f.print("<TR><TD BGCOLOR=" + color(helper.getPercent(match), 128, 192, 128, 192, 255, 255) + ">" + name
									+ "</TD><TD><nobr>-&gt;</nobr>");
						}
						float percent = helper.getPercent(output);
						f.print("</TD><TD BGCOLOR=" + color(percent, 128, 192, 128, 192, 255, 255) + " ALIGN=center><A HREF=\"match"
								+ getMatchIndex(output) + ".html\">" + output.subName(other) + "</A><BR><FONT COLOR=\""
								+ color(percent, 0, 255, 0, 0, 0, 0) + "\">(" + (((int) (percent * 10)) / (float) 10) + "%)</FONT>");
					}
				}
				if (header)
					f.println("</TD></TR>");
			}
		}

		f.println("</TABLE><P>\n");
		f.println("<!---->"); // important for front end
	}


	private void writeMatchesCSV( File root, String filename, SortedVector<AllMatches> matches, MatchesHelper helper) {

		// quick and very dirty csv export of results

		FileWriter writer = null;
		File f = new File(root, filename);

		try{

			f.createNewFile();
			writer = new FileWriter(f);

			//		output all the matches
			//		Set<String> namesPrinted = new Set();
			Set<AllMatches> matchesPrinted = new HashSet<AllMatches>();


			int anz = matches.size();
			for (int i = 0; ((i < anz) && (matchesPrinted.size() != anz)); i++) {
				AllMatches match = matches.elementAt(i);
				if (!matchesPrinted.contains(match)) {
					//				!namesPrinted.contains(match.subName(j))) {
					int a = 0, b = 0;
					String nameA = match.subName(0);
					String nameB = match.subName(1);
					//				Which of both submissions is referenced more often in "matches"?
					for (int x = 0; x < anz; x++) {
						AllMatches tmp = matches.elementAt(x);
						if (tmp != match && !matchesPrinted.contains(tmp)) {
							String tmpA = tmp.subName(0);
							String tmpB = tmp.subName(1);
							if (nameA.equals(tmpA) || nameA.equals(tmpB))
								a += helper.getPercent(tmp);
							if (nameB.equals(tmpA) || nameB.equals(tmpB))
								b += helper.getPercent(tmp);
						}
					}
					String name = (a >= b ? nameA : nameB);
					boolean header = false;
					//				namesPrinted.put(name);

					AllMatches output;
					for (int x = 0; x < anz; x++) {
						output = matches.elementAt(x);
						if (!matchesPrinted.contains(output) && (output.subName(0).equals(name) || output.subName(1).equals(name))) {
							matchesPrinted.add(output);
							int other = (output.subName(0).equals(name) ? 1 : 0);
							if (!header) { // only print header when necessary!
								header = true;
								writer.write(name + ";");
							}
							float percent = helper.getPercent(output);
							writer.write(getMatchIndex(output) + ";");
							writer.write(output.subName(other) + ";");
							writer.write( (((int) (percent * 10)) / (float) 10) + ";");

						}
					}
					if (header)
						writer.write("\n");
				}
			}

			writer.flush();

		} catch (Exception e) {
			// POC: ignore all errors
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}


	}

	private int writeIndex(boolean includeClusterLink) throws jplag.ExitException {
		HTMLFile f = openHTMLFile(root, "index.html");

		writeIndexBegin(f, msg.getString("Report.Search_Results"));

		writeDistribution(f);

		String csvfile="matches_avg.csv";
		writeLinksToMatches(f, avgmatches, new MatchesHelper() {
			public float getPercent(AllMatches matches) {
				return matches.percent();
			}
		}, "<H4>" + msg.getString("Report.MatchesAvg"), csvfile);

		writeMatchesCSV(root, csvfile, avgmatches,
			new MatchesHelper() {
				 public float getPercent(AllMatches matches) {
				   return matches.percent();
				 }
			}
		 );

		if (minmatches != null){
			csvfile="matches_min.csv";
			writeLinksToMatches(f, minmatches, new MatchesHelper() {
				public float getPercent(AllMatches matches) {
					return matches.percentMinAB();
				}
			}, "<HR><H4>" + msg.getString("Report.MatchesMin"), csvfile);

			writeMatchesCSV(root, csvfile, avgmatches,
				new MatchesHelper() {
					 public float getPercent(AllMatches matches) {
					   return matches.percentMinAB();
					 }
				}
			 );
		};


		if (maxmatches != null){
			csvfile="matches_max.csv";
			writeLinksToMatches(f, maxmatches, new MatchesHelper() {
				public float getPercent(AllMatches matches) {
					return matches.percentMaxAB();
				}
			}, "<HR><H4>" + msg.getString("Report.MatchesMax"), csvfile);

			writeMatchesCSV(root, csvfile, avgmatches,
				new MatchesHelper() {
					 public float getPercent(AllMatches matches) {
					   return matches.percentMaxAB();
					 }
				}
			 );
		};


		if (includeClusterLink) {
			f.println("<HR><H4><A HREF=\"cluster.html\">" + msg.getString("Report.Clustering_Results") + "</A></H4>");
		}

		writeIndexEnd(f);

		f.close();

		int size = f.bytesWritten();

		// a few infos are saved into a textfile- used in the server environment
		if (this.program.get_original_dir() != null) {
			f = openHTMLFile(root, "info.txt");
			f.println("directory" + "\t" + program.get_original_dir()
					+ (program.get_sub_dir() != null ? File.separator + "*" + File.separator + program.get_sub_dir() : ""));
			f.println("language" + "\t" + language.name());
			f.println("submissions" + "\t" + program.validSubmissions());
			f.println("errors" + "\t" + program.getErrors());
			f.close();
		}

		return size + f.bytesWritten();
	}

	public void writeIndexBegin(HTMLFile f, String title) {
		writeHTMLHeader(f, title);
		f.println("<BODY BGCOLOR=#ffffff LINK=#000088 VLINK=#000000 TEXT=#000000>");
		f.println("<TABLE ALIGN=center CELLPADDING=2 CELLSPACING=1>");
		f.println("<TR VALIGN=middle ALIGN=center BGCOLOR=#ffffff><TD>" + "<IMG SRC=\"logo.gif\" ALT=\"JPlag\" BORDER=0></TD>");
		f.println("<TD><H1><BIG>" + title + "</BIG></H1></TD></TR>");

		if (program.get_title() != null) {
			f.println("<TR BGCOLOR=\"#aaaaff\" VALIGN=\"top\"><TD>" + "<BIG><BIG>" + msg.getString("Report.Title")
					+ ":</BIG></BIG><TD><BIG><BIG><CODE>" + program.get_title() + "</CODE></BIG></BIG></TD></TR>");
			if (program.get_original_dir() != null)
				f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + "<BIG>" + msg.getString("Report.Directory")
						+ ":</BIG></TD><TD><BIG><CODE>" + program.get_original_dir()
						+ (program.get_sub_dir() == null ? "" : File.separator + "*" + File.separator + program.get_sub_dir())
						+ "</CODE></BIG></TD></TR>");
		} else {
			if (this.program.get_original_dir() == null)
				f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + "<BIG><BIG>" + msg.getString("Report.Directory")
						+ ":</BIG></BIG><TD><BIG><BIG><CODE>" + msg.getString("Report.Not_available") + "</CODE></BIG></BIG></TD></TR>");
			else
				f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + "<BIG><BIG>" + msg.getString("Report.Directory")
						+ ":</BIG></BIG></TD><TD><BIG><BIG><CODE>" + program.get_original_dir()
						+ (program.get_sub_dir() == null ? "" : File.separator + "*" + File.separator + program.get_sub_dir())
						+ "</CODE></BIG></BIG></TD></TR>");
		}

		f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Programs") + ":</TD><TD>");
		f.println("<CODE>" + program.allValidSubmissions(" - ") + "</CODE></TD></TR>");
		f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Language") + ":</TD><TD>" + this.language.name()
				+ "</TD></TR>");
		f.print("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Submissions") + ":</TD><TD>"
				+ this.program.validSubmissions());
		if (program.getErrors() != 0) {
			if (this.program.getErrors() == 1)
				f.print(" <b>(" + msg.getString("Report.1_has_not_been_parsed_successfully") + ")</b>");
			else if (this.program.getErrors() > 1)
				f.print(" <b>("
						+ TagParser.parse(msg.getString("Report.X_have_not_been_parsed_successfully"), new String[] { program.getErrors()
								+ "" }) + ")</b>");
			f.println("</TD></TR>");
			f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Invalid_submissions"));
			if (options.output_file != null) {
				f.println(" "
						+ TagParser.parse(msg.getString("Report.see_LOGBEG_log_file_LOGEND"), new String[] {
								"<a href=\"" + options.output_file.substring(options.output_file.lastIndexOf(File.separatorChar) + 1)
										+ "\">", "</a>" }));
			}
			f.println(":</TD><TD>");
			f.println("<CODE>" + this.program.allInvalidSubmissions() + "</CODE>");
		}
		f.println("</TD></TR>");
		if (this.program.useBasecode()) {
			f.print("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Basecode_submission") + ":</TD>" + "<TD>"
					+ this.program.get_basecode() + "</TD></TR>");
		}
		if (avgmatches != null && avgmatches.size() > 0 || minmatches != null && minmatches.size() > 0 || maxmatches != null
				&& maxmatches.size() > 0) {
			f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Matches_displayed") + ":</TD>" + "<TD>");
			if (avgmatches != null && avgmatches.size() > 0)
				f.println(avgmatches.size() + " (" + msg.getString("Report.Treshold") + ": "
						+ avgmatches.elementAt(avgmatches.size() - 1).roundedPercent() + "%) ("
						+ msg.getString("Report.average_similarity") + ")<br>");
			if (minmatches != null && minmatches.size() > 0)
				f.println(minmatches.size() + " (" + msg.getString("Report.Treshold") + ": "
						+ minmatches.elementAt(minmatches.size() - 1).roundedPercentMinAB() + "%) ("
						+ msg.getString("Report.minimum_similarity") + ")<br>");
			if (maxmatches != null && maxmatches.size() > 0)
				f.println(maxmatches.size() + " (" + msg.getString("Report.Treshold") + ": "
						+ maxmatches.elementAt(maxmatches.size() - 1).roundedPercentMaxAB() + "%) ("
						+ msg.getString("Report.maximum_similarity") + ")<br>");
			f.println("</TD></TR>");
		}

		/*
		 * DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, new
		 * Locale(program.getCountryTag()));
		 */

		f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Date") + ":</TD><TD>"
				+ program.dateFormat.format(new Date()) + "</TD></TR>");
		f.println("<TR BGCOLOR=#aaaaff>" + "<TD><EM>" + msg.getString("Report.Minimum_Match_Length") + "</EM> ("
				+ msg.getString("Report.sensitivity") + "):</TD><TD>" + program.get_min_token_match() + "</TD></TR>");

		f.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Suffixes") + ":</TD><TD>");
		for (int i = 0; i < this.program.get_suffixes().length; i++)
			f.print(this.program.get_suffixes()[i] + (i < this.program.get_suffixes().length - 1 ? ", " : "</TD></TR>\n"));
		f.println("</TABLE>\n<HR>");
	}

	public void writeIndexEnd(HTMLFile f) {
		f.println("<HR>\n<P ALIGN=right><FONT SIZE=\"1\" FACE=\"helvetica\">" + Program.name + "</FONT></P>");
		f.println("</BODY>\n</HTML>");
	}

	/* this function copies all submissions into the result directory */
	private int copySubmissions() throws jplag.ExitException {
		int bytes = 0;
		for (Iterator<Submission> i = program.clusters.neededSubmissions.iterator(); i.hasNext();) {
			Submission sub = i.next();
			int index = this.program.clusters.submissions.indexOf(sub);

			HTMLFile f = openHTMLFile(root, "submission" + index + ".html");
			writeHTMLHeader(f, sub.name);
			f.println("<BODY BGCOLOR=\"#ffffff\">");

			String[] files = sub.files;
			String text[][] = sub.readFiles(files);

			for (int j = 0; j < files.length; j++) {
				f.println("<HR>\n<H3><CENTER>" + files[j] + "</CENTER></H3><HR>");
				if (this.language.isPreformated())
					f.println("<PRE>");
				for (int k = 0; k < text[j].length; k++) {
					f.print(text[j][k]);
					if (!this.language.isPreformated())
						f.println("<BR>");
					else
						f.println();
				}
				if (language.isPreformated())
					f.println("</PRE>");
			}

			f.println("</BODY>\n</HTML>");
			f.close();
			bytes += f.bytesWritten();
		}
		return bytes;
	}

	private int writeClusters(Cluster clustering) throws jplag.ExitException {
		int bytes = 0;

		HTMLFile f = openHTMLFile(root, "cluster.html");
		writeHTMLHeader(f, msg.getString("Report.Clustering_Results"));
		String clustertype = msg.getString("Report.Type") + ": " + program.clusters.getType();
		f.println("<BODY>\n<H2>" + msg.getString("Report.Clustering_Results") + " (" + clustertype + ")</H2>");
		f.println("<H3><A HREF=\"dendro.html\">" + msg.getString("Report.Dendrogram") + "</A></H3>");
		bytes += this.program.clusters.makeDendrograms(root, clustering);

		if (this.program.get_threshold() != null) {
			for (int i = 0; i < this.program.get_threshold().length; i++) {
				float threshold = this.program.get_threshold()[i];
				String clustertitle = TagParser.parse(msg.getString("Report.Clusters_for_Xpercent_treshold"),
						new String[] { threshold + "" });
				f.println("<H3><A HREF=\"cluster" + threshold + ".html\">" + clustertitle + "</A></H3>");
				HTMLFile f2 = openHTMLFile(root, "cluster" + threshold + ".html");
				writeHTMLHeader(f2, clustertitle);
				f2.println("<BODY>\n<H2>" + clustertitle + " (" + clustertype + ")</H2>");
				String text = program.clusters.printClusters(clustering, threshold, f2);
				f2.println("</BODY>\n</HTML>");
				f2.close();
				bytes += f2.bytesWritten();
				f.print(text);
			}
		} else {
			float increase = this.program.clusters.maxMergeValue / 10;
			if (increase < 5)
				increase = 5;
			for (float threshold = increase; threshold <= program.clusters.maxMergeValue; threshold += increase) {
				String clustertitle = TagParser.parse(msg.getString("Report.Clusters_for_Xpercent_treshold"),
						new String[] { threshold + "" });
				f.println("<H3><A HREF=\"cluster" + (int) threshold + ".html\">" + clustertitle + "</A></H3>");
				HTMLFile f2 = openHTMLFile(root, "cluster" + (int) threshold + ".html");
				writeHTMLHeader(f2, clustertitle);
				f2.println("<BODY>\n<H2>" + clustertitle + " (" + clustertype + ")</H2>");
				String text = program.clusters.printClusters(clustering, (int) threshold, f2);
				f2.println("</BODY>\n</HTML>");
				f2.close();
				bytes += f2.bytesWritten();
				f.print(text);
			}
		}

		f.println("</BODY>\n</HTML>");
		f.close();

		bytes += copySubmissions();
		return f.bytesWritten() + bytes;
	}

	/*
	 * Two colors, represented by Rl,Gl,Bl and Rh,Gh,Bh respectively are mixed
	 * according to the percentage "percent"
	 */
	public final String color(float percent, int Rl, int Rh, int Gl, int Gh, int Bl, int Bh) {
		int farbeR = (int) (Rl + (Rh - Rl) * percent / 100);
		int farbeG = (int) (Gl + (Gh - Gl) * percent / 100);
		int farbeB = (int) (Bl + (Bh - Bl) * percent / 100);
		String helpR = (farbeR < 16 ? "0" : "") + Integer.toHexString(farbeR);
		String helpG = (farbeG < 16 ? "0" : "") + Integer.toHexString(farbeG);
		String helpB = (farbeB < 16 ? "0" : "") + Integer.toHexString(farbeB);
		return "#" + helpR + helpG + helpB;
	}

	// MATCHES
	private void writeMatches(SortedVector<AllMatches> matches) throws jplag.ExitException {
		Enumeration<AllMatches> enum1 = matches.elements();
		for (int i = 0; enum1.hasMoreElements(); i++) {
			AllMatches match = enum1.nextElement();
			if (!matchesIndexMap.containsKey(match))
				continue; // match has already been written
			if (this.program.use_externalSearch()) {
				ThemeGenerator.loadStructure(match.subA);
				ThemeGenerator.loadStructure(match.subB);
			}
			writeMatch(root, getMatchIndex(match), match);
			matchesIndexMap.remove(match); // "mark" as already written
			options.setProgress((i + 1) * 100 / matches.size());

			if (this.program.use_externalSearch()) {
				match.subA.struct = null;
				match.subB.struct = null;
			}
		}
	}

	public int writeMatch(File root, int i, AllMatches match) throws jplag.ExitException {
		this.root = root;
		int bytes = 0;
		// match???.html
		bytes = writeFrames(i, match);
		// match???-link.html
		bytes += writeLink(i, match);
		// match???-top.html
		bytes += writeTop(i, match);
		// match???-dist.html
		if (this.program.use_externalSearch()) {
			bytes += writeDist(i, match);
		}
		// match???-?.html
		if (!this.program.use_diff_report()) {
			if (this.language.usesIndex()) {
				bytes += writeIndexedSubmission(i, match, 0);
				bytes += writeIndexedSubmission(i, match, 1);
			} else if (this.language.supportsColumns()) {
				bytes += writeImprovedSubmission(i, match, 0);
				bytes += writeImprovedSubmission(i, match, 1);
			} else {
				bytes += writeNormalSubmission(i, match, 0);
				bytes += writeNormalSubmission(i, match, 1);
			}
		} else {
			bytes += writeSubmissionDiff(i, match, 0);
			bytes += writeSubmissionDiff(i, match, 1);
		}
		return bytes;
	}

	private int writeFrames(int i, AllMatches match) throws jplag.ExitException {
		HTMLFile f = openHTMLFile(root, "match" + i + ".html");
		writeHTMLHeader(f,
				TagParser.parse(msg.getString("Report.Matches_for_X1_AND_X2"), new String[] { match.subName(0), match.subName(1) }));
		f.println("<FRAMESET ROWS=\"130,*\">\n <FRAMESET COLS=\"30%,70%\">");
		f.println("  <FRAME SRC=\"match" + i + "-link.html\" NAME=\"link\" " + "FRAMEBORDER=0>");
		f.println("  <FRAME SRC=\"match" + i + "-top.html\" NAME=\"top\" " + "FRAMEBORDER=0>");
		f.println(" </FRAMESET>");
		f.println(" <FRAMESET COLS=\"50%,50%\">");
		f.println("  <FRAME SRC=\"match" + i + "-0.html\" NAME=\"0\">");
		f.println("  <FRAME SRC=\"match" + i + "-1.html\" NAME=\"1\">");
		f.println(" </FRAMESET>\n</FRAMESET>\n</HTML>");
		f.close();
		return f.bytesWritten();
	}

	private int writeLink(int i, AllMatches match) throws jplag.ExitException {
		HTMLFile f = openHTMLFile(root, "match" + i + "-link.html");
		writeHTMLHeader(f, msg.getString("Report.Links"));
		f.println("<BODY>\n <H3 ALIGN=\"center\">"
				+ TagParser.parse(msg.getString("Report.Matches_for_X1_AND_X2"), new String[] { match.subName(0), match.subName(1) })
				+ "</H3>");
		f.println(" <H1 align=\"center\">" + match.roundedPercent() + "%</H1>\n<CENTER>");
		f.println(" <A HREF=\"index.html#matches\" TARGET=\"_top\">" + msg.getString("Report.INDEX") + "</A> - ");
		f.println(" <A HREF=\"help-" + program.getCountryTag() + ".html\" TARGET=\"_top\">" + msg.getString("Report.HELP")
				+ "</A></CENTER>");
		f.println("</BODY>\n</HTML>");
		f.close();
		return f.bytesWritten();
	}

	private int writeTop(int i, AllMatches match) throws jplag.ExitException {
		HTMLFile f = openHTMLFile(root, "match" + i + "-top.html");
		writeHTMLHeaderWithScript(f, "Top");
		f.println("<BODY BGCOLOR=\"#ffffff\">");

		if (this.program.use_externalSearch()) {
			f.println("<A HREF=\"match" + i + "-dist.html\" TARGET=\"_top\">" + msg.getString("Report.Distribution") + "</A><P>");
		}

		match.HTMLreport(f, i, this.program);

		f.println("</BODY>\n</HTML>\n");
		f.close();
		return f.bytesWritten();
	}

	private int writeDist(int i, AllMatches match) throws jplag.ExitException {
		HTMLFile f = openHTMLFile(root, "match" + i + "-dist.html");
		writeHTMLHeader(f, msg.getString("Report.Token_Distribution"));
		f.println("<BODY>");
		match.distributionReport(f, msg);
		f.println("</BODY>\n</HTML>");
		f.close();
		return f.bytesWritten();
	}

	// SUBMISSION - here it comes...
	private final String[] pics = { "forward.gif", "back.gif" };

	/*
	 * i is the number of the match j == 0 if subA is considered, otherwise (j
	 * must then be 1) it is subB
	 */
	private int writeNormalSubmission(int i, AllMatches match, int j) throws jplag.ExitException {
		Submission sub = (j == 0 ? match.subA : match.subB);
		String files[] = match.files(j);

		String[][] text = sub.readFiles(files);

		Token[] tokens = (j == 0 ? match.subA : match.subB).struct.tokens;
		Match onematch;
		String hilf;
		int h;
		for (int x = 0; x < match.size(); x++) {
			onematch = match.matches[x];

			Token start = tokens[(j == 0 ? onematch.startA : onematch.startB)];
			Token ende = tokens[((j == 0 ? onematch.startA : onematch.startB) + onematch.length - 1)];

			for (int y = 0; y < files.length; y++) {
				if (start.file.equals(files[y]) && text[y] != null) {
					hilf = "<FONT color=\"" + Colors.getColor(x) + "\">" + (j == 1 ? "<div style=\"position:absolute;left:0\">" : "")
							+ "<A HREF=\"javascript:ZweiFrames('match" + i + "-" + (1 - j) + ".html#" + x + "'," + (3 - j) + ",'match" + i
							+ "-top.html#" + x + "',1)\"><IMG SRC=\"" + pics[j] + "\" ALT=\"other\" " + "BORDER=\"0\" ALIGN=\""
							+ (j == 0 ? "right" : "left") + "\"></A>" + (j == 1 ? "</div>" : "") + "<B>";
					// position the icon and the beginning of the colorblock
					if (text[y][start.getLine() - 1].endsWith("</FONT>"))
						text[y][start.getLine() - 1] += hilf;
					else
						text[y][start.getLine() - 1] = hilf + text[y][start.getLine() - 1];
					// the link location is placed 3 lines before the start of a block
					h = (start.getLine() - 4 < 0 ? 0 : start.getLine() - 4);
					text[y][h] = "<A NAME=\"" + x + "\"></A>" + text[y][h];
					// mark the end
					if (start.getLine() != ende.getLine() && // if match is only one line
							text[y][ende.getLine() - 1].startsWith("<FONT "))
						text[y][ende.getLine() - 1] = "</B></FONT>" + text[y][ende.getLine() - 1];
					else
						text[y][ende.getLine() - 1] += "</B></FONT>";
				}
			}
		}

		if (this.program.useBasecode() && match.bcmatchesA != null && match.bcmatchesB != null) {
			AllBasecodeMatches bcmatch = (j == 0 ? match.bcmatchesA : match.bcmatchesB);
			for (int x = 0; x < bcmatch.size(); x++) {
				onematch = bcmatch.matches[x];
				Token start = tokens[onematch.startA];
				Token ende = tokens[onematch.startA + onematch.length - 1];

				for (int y = 0; y < files.length; y++) {
					if (start.file.equals(files[y]) && text[y] != null) {
						hilf = ("<font color=\"#C0C0C0\"><EM>");
						// position the icon and the beginning of the colorblock
						if (text[y][start.getLine() - 1].endsWith("<font color=\"#000000\">"))
							text[y][start.getLine() - 1] += hilf;
						else
							text[y][start.getLine() - 1] = hilf + text[y][start.getLine() - 1];

						// mark the end
						if (start.getLine() != ende.getLine() && // match is only one line
								text[y][ende.getLine() - 1].startsWith("<font color=\"#C0C0C0\">"))
							text[y][ende.getLine() - 1] = "</EM><font color=\"#000000\">" + text[y][ende.getLine() - 1];
						else
							text[y][ende.getLine() - 1] += "</EM><font color=\"#000000\">";
					}
				}
			}
		}

		HTMLFile f = openHTMLFile(root, "match" + i + "-" + j + ".html");
		writeHTMLHeaderWithScript(f, (j == 0 ? match.subA : match.subB).name);
		f.println("<BODY BGCOLOR=\"#ffffff\"" + (j == 1 ? " style=\"margin-left:25\">" : ">"));

		for (int x = 0; x < text.length; x++) {
			f.println("<HR>\n<H3><CENTER>" + files[x] + "</CENTER></H3><HR>");
			if (this.language.isPreformated())
				f.println("<PRE>");
			for (int y = 0; y < text[x].length; y++) {
				f.print(text[x][y]);
				if (!this.language.isPreformated())
					f.println("<BR>");
				else
					f.println();
			}
			if (this.language.isPreformated())
				f.println("</PRE>");
		}
		f.println("</BODY>\n</HTML>");
		f.close();
		return f.bytesWritten();
	}

	/*
	 * i is the number of the match j == 0 if subA is considered, otherwise it
	 * is subB
	 * 
	 * This procedure uses only the getIndex() method of the token. It is meant
	 * to be used with the Character front end
	 */
	private int writeIndexedSubmission(int i, AllMatches match, int j) throws jplag.ExitException {
		Submission sub = (j == 0 ? match.subA : match.subB);
		String files[] = match.files(j);
		char[][] text = sub.readFilesChar(files);
		Token[] tokens = (j == 0 ? match.subA : match.subB).struct.tokens;

		// get index array with matches sorted in ascending order.
		int[] perm = match.sort_permutation(j);

		// HTML intro
		HTMLFile f = openHTMLFile(root, "match" + i + "-" + j + ".html");
		writeHTMLHeaderWithScript(f, (j == 0 ? match.subA : match.subB).name);
		f.println("<BODY BGCOLOR=\"#ffffff\">");

		int index = 0; // match index
		Match onematch = null;
		Token start = null;
		Token end = null;
		for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
			// print filename
			f.println("<HR>\n<H3><CENTER>" + files[fileIndex] + "</CENTER></H3><HR>");
			char[] buffer = text[fileIndex];

			for (int charNr = 0; charNr < buffer.length; charNr++) {
				if (onematch == null) {
					if (index < match.size()) {
						onematch = match.matches[perm[index]];
						start = tokens[(j == 0 ? onematch.startA : onematch.startB)];
						end = tokens[((j == 0 ? onematch.startA : onematch.startB) + onematch.length - 1)];
						index++;
					} else {
						start = end = null;
					}
				}
				// begin markup
				if (start != null && start.getIndex() == charNr) {
					f.print("<A NAME=\"" + perm[index - 1] + "\"></A>");
					f.print("<FONT color=\"" + Colors.getColor(perm[index - 1]) + "\"><B>");
					//"<A HREF=\"javascript:ZweiFrames('match"+i+"-"+(1-j)+
					//".html#"+index+"',"+(3-j)+",'match"+i+"-top.html#"+index+
					//"',1)\">"+"<IMG SRC=\""+pics[j]+
					//"\" ALT=\"other\" BORDER=\"0\" "+"ALIGN="+
					//(j==0 ? "right" : "left")+"></A>");
				}
				// text
				if (buffer[charNr] == '<') {
					f.print("&lt;");
				} else if (buffer[charNr] == '>') {
					f.print("&gt;");
				} else if (buffer[charNr] == '\n') {
					f.print("<br>\n");
				} else
					f.print(buffer[charNr]);
				// end markup
				if (end != null && end.getIndex() == charNr) {
					f.print("</B></FONT>");
					onematch = null; // switch to next match
				}
			}
		}

		f.println("\n</BODY>\n</HTML>");
		f.close();
		return f.bytesWritten();
	}

	/*
	 * i is the number of the match j == 0 if subA is considered, otherwise (j
	 * must then be 1) it is subB
	 * 
	 * This procedure makes use of the column and length information!
	 */
	private int writeImprovedSubmission(int i, AllMatches match, int j) throws jplag.ExitException {
		Submission sub = (j == 0 ? match.subA : match.subB);
		String files[] = match.files(j);
		String[][] text = sub.readFiles(files);
		Token[] tokens = (j == 0 ? match.subA : match.subB).struct.tokens;

		// Markup list:
		Comparator<MarkupText> comp = new Comparator<MarkupText>() {
			public int compare(MarkupText mo1, MarkupText mo2) {
				int col1 = mo1.column;
				int col2 = mo2.column;
				if (col1 > col2)
					return -1;
				else if (col1 < col2)
					return 1;
				return (mo1.frontMarkup ? -1 : 1);
			}
		};
		TreeMap<MarkupText, Object> markupList = new TreeMap<MarkupText, Object>(comp);

		for (int x = 0; x < match.size(); x++) {
			Match onematch = match.matches[x];

			Token start = tokens[(j == 0 ? onematch.startA : onematch.startB)];
			Token end = tokens[((j == 0 ? onematch.startA : onematch.startB) + onematch.length - 1)];
			for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
				if (start.file.equals(files[fileIndex]) && text[fileIndex] != null) {
					String tmp = "<FONT color=\"" + Colors.getColor(x) + "\">" + (j == 1 ? "<div style=\"position:absolute;left:0\">" : "")
							+ "<A HREF=\"javascript:ZweiFrames('match" + i + "-" + (1 - j) + ".html#" + x + "'," + (3 - j) + ",'match" + i
							+ "-top.html#" + x + "',1)\"><IMG SRC=\"" + pics[j] + "\" ALT=\"other\" " + "BORDER=\"0\" ALIGN=\""
							+ (j == 0 ? "right" : "left") + "\"></A>" + (j == 1 ? "</div>" : "") + "<B>";
					// position the icon and the beginning of the colorblock
					markupList.put(new MarkupText(fileIndex, start.getLine() - 1, start.getColumn() - 1, tmp, true), null);
					// mark the end
					markupList
							.put(new MarkupText(fileIndex, end.getLine() - 1, end.getColumn() + end.getLength() - 1, "</B></FONT>", false),
									null);

					// the link location is placed 3 lines before the start of a block
					int linkLine = (start.getLine() - 4 < 0 ? 0 : start.getLine() - 4);
					markupList.put(new MarkupText(fileIndex, linkLine, 0, "<A NAME=\"" + x + "\"></A>", false), null);
				}
			}
		}

		if (this.program.useBasecode() && match.bcmatchesA != null && match.bcmatchesB != null) {
			AllBasecodeMatches bcmatch = (j == 0 ? match.bcmatchesA : match.bcmatchesB);
			for (int x = 0; x < bcmatch.size(); x++) {
				Match onematch = bcmatch.matches[x];
				Token start = tokens[onematch.startA];
				Token end = tokens[onematch.startA + onematch.length - 1];

				for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
					if (start.file.equals(files[fileIndex]) && text[fileIndex] != null) {
						String tmp = "<font color=\"#C0C0C0\"><EM>";
						// beginning of the colorblock
						markupList.put(new MarkupText(fileIndex, start.getLine() - 1, start.getColumn() - 1, tmp, false), null);
						// mark the end
						markupList.put(new MarkupText(fileIndex, end.getLine() - 1, end.getColumn() + end.getLength() - 1, "</EM></font>",
								true), null);
					}
				}
			}
		}

		// Apply changes:
		for (Iterator<MarkupText> iter = markupList.keySet().iterator(); iter.hasNext();) {
			MarkupText markup = iter.next();
			//System.out.println(markup);
			String tmp = text[markup.fileIndex][markup.lineIndex];
			// is there any &quot;, &amp;, &gt; or &lt; in the String? 
			if (tmp.indexOf('&') >= 0) {
				Vector<String> tmpV = new Vector<String>();
				// convert the string into a vector
				int strLength = tmp.length();
				for (int k = 0; k < strLength; k++) {
					if (tmp.charAt(k) != '&')
						tmpV.addElement(tmp.charAt(k) + "");
					else { //put &quot;, &amp;, &gt; and &lt; into one element
						String tmpSub = tmp.substring(k);
						if (tmpSub.startsWith("&quot;")) {
							tmpV.addElement("&quot;");
							k = k + 5;
						} else if (tmpSub.startsWith("&amp;")) {
							tmpV.addElement("&amp;");
							k = k + 4;
						} else if (tmpSub.startsWith("&lt;")) {
							tmpV.addElement("&lt;");
							k = k + 3;
						} else if (tmpSub.startsWith("&gt;")) {
							tmpV.addElement("&gt;");
							k = k + 3;
						} else
							tmpV.addElement(tmp.charAt(k) + "");
					}
				}
				if (markup.column <= tmpV.size()) {
					tmpV.insertElementAt(markup.text, markup.column);
				} else {
					tmpV.addElement(markup.text);
				}

				String tmpVStr = "";
				// reconvert the Vector into a String
				for (int k = 0; k < tmpV.size(); k++)
					tmpVStr = tmpVStr + tmpV.elementAt(k);
				text[markup.fileIndex][markup.lineIndex] = tmpVStr;
			} else {
				text[markup.fileIndex][markup.lineIndex] = tmp.substring(0, (tmp.length() > markup.column ? markup.column : tmp.length()))
						+ markup.text + tmp.substring((tmp.length() > markup.column ? markup.column : tmp.length()));
			}
		}

		HTMLFile f = openHTMLFile(root, "match" + i + "-" + j + ".html");
		writeHTMLHeaderWithScript(f, (j == 0 ? match.subA : match.subB).name);
		f.println("<BODY BGCOLOR=\"#ffffff\"" + (j == 1 ? " style=\"margin-left:25\">" : ">"));

		for (int x = 0; x < text.length; x++) {
			f.println("<HR>\n<H3><CENTER>" + files[x] + "</CENTER></H3><HR>");
			if (this.language.isPreformated())
				f.println("<PRE>");
			for (int y = 0; y < text[x].length; y++) {
				f.print(text[x][y]);
				if (!this.language.isPreformated())
					f.println("<BR>");
				else
					f.println();
			}
			if (this.language.isPreformated())
				f.println("</PRE>");
		}
		f.println("\n</BODY>\n</HTML>");
		f.close();
		return f.bytesWritten();
	}

	/*
	 * i is the number of the match j == 0 if subA is considered, otherwise it
	 * is subB
	 */
	private int writeSubmissionDiff(int i, AllMatches match, int j) throws jplag.ExitException {
		Submission sub = (j == 0 ? match.subA : match.subB);
		String files[] = match.allFiles(j);

		String[][] text = sub.readFiles(files);
		for (int x = 0; x < text.length; x++) {
			for (int line = 0; line < text[x].length; line++) {
				switch (match.diffType(files[x], line + 1, j)) {
				case 0:
					text[x][line] = "<FONT COLOR=\"#000000\">" + text[x][line] + "</FONT>";
					break;
				case 1:
					text[x][line] = "<FONT COLOR=\"#0000FF\">" + text[x][line] + "</FONT>";
					break;
				case 2:
				default:
					text[x][line] = "<FONT COLOR=\"#FF0000\">" + text[x][line] + "</FONT>";
					break;
				}
			}
		}

		HTMLFile f = openHTMLFile(root, "match" + i + "-" + j + ".html");
		writeHTMLHeader(f, (j == 0 ? match.subA : match.subB).name);
		f.println("<BODY>");

		for (int x = 0; x < text.length; x++) {
			f.println("<HR>\n<H3><CENTER>" + files[x] + "</CENTER></H3><HR>\n<PRE>");
			for (int y = 0; y < text[x].length; y++)
				f.println(text[x][y]);
			f.println("</PRE>");
		}
		f.println("</BODY>\n</HTML>");
		f.close();
		return f.bytesWritten();
	}

	/*
	 * This procedure copies all the data from "data/" into the
	 * result-directory.
	 */
	private String[] fileList = { "back.gif", "forward.gif", "help-en.html", "help-sim-en.html", "logo.gif", "fields.js" };

	public void copyFixedFiles(File root) {
		fileList[2] = "help-" + program.getCountryTag() + ".html";
		fileList[3] = "help-sim-" + program.getCountryTag() + ".html";
		for (int i = fileList.length - 1; i >= 0; i--) {
			try {
				java.net.URL url = Report.class.getResource("data/" + fileList[i]);
				DataInputStream dis = new DataInputStream(url.openStream());

				File dest = new File(root, fileList[i]);
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(dest));

				byte[] buffer = new byte[1024];
				int count;
				do {
					count = dis.read(buffer);
					if (count != -1)
						dos.write(buffer, 0, count);
				} while (count != -1);
				dis.close();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
}

/**
 * This class represents one markup tag that will be included in the text. It is
 * necessary to sort the objects before they are included into the text, so that
 * the original position can be found.
 */
class MarkupText {
	public int fileIndex, lineIndex, column;
	public String text;
	public boolean frontMarkup = false;

	public MarkupText(int fileIndex, int lineIndex, int column, String text, boolean frontMarkup) {
		this.fileIndex = fileIndex;
		this.lineIndex = lineIndex;
		this.column = column;
		this.text = text;
		this.frontMarkup = frontMarkup;
	}

	public String toString() {
		return "MarkUp - file: " + fileIndex + " line: " + lineIndex + " column: " + column + " text: " + text;
	}
}
