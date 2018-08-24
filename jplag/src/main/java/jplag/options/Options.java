/*
 * Author  Emeric Kwemou on 30.01.2005
 * this small package provide an interface to generate options of a jplag.Program instance
 * all type of options will be derivated from the abstract class Options. A simple implementation
 * is for exmaple a class that initialize commandline options.
 *
 * 
 */
package jplag.options;

import jplag.Language;
import jplag.Program;
import jplag.clustering.SimilarityMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Emeric Kwemou 30.01.2005
 *  
 */
public abstract class Options {
	protected String langs = "jplag.options.util.messages_en";

	protected boolean suffixes_set = false;

	protected boolean min_token_match_set = false;

	/**
	 * The available languages - the first language in the array is the default language.
	 */
	protected static String[] languages = {// @formatter:off
			"java17", "jplag.java17.Language",
			"java15", "jplag.java15.Language",
			"java15dm", "jplag.java15.LanguageWithDelimitedMethods",
			"java12", "jplag.java.Language",
			"java11", "jplag.javax.Language",
			"python3","jplag.python3.Language",
			"c/c++", "jplag.cpp.Language",
			"c#-1.2", "jplag.csharp.Language",
			"char", "jplag.chars.Language",
			"text", "jplag.text.Language",
			"scheme", "jplag.scheme.Language",
			"scala", "jplag.scala.Language",
			"json", "jplag.json.Language",
			"php", "jplag.generic.PhpLanguage",
			"javascript", "jplag.generic.JavascriptLanguage",
			"jupyter", "jplag.ipython.IPythonLanguage",
			"r","jplag.R.Language",
			};// @formatter:on

	public static final int MIN_CLUSTER = 1;
	public static final int MAX_CLUSTER = 2;
	public static final int AVR_CLUSTER = 3;

	// Program OPTIONS
	public boolean verbose_quiet = false;

	public boolean verbose_long = false;

	public boolean verbose_parser = false;

	public boolean verbose_details = false;

	// use to detect a language in the initilalisation
	public boolean languageIsFound = false;

	public String sub_dir = null;

	public String root_dir = null;

	public String original_dir = null;
	
	public String title = "";

	public String output_file = null;

	public String exclude_file = null;

	public String include_file = null;

	public boolean read_subdirs = false;

	public int store_matches = 30;

	public boolean store_percent = false; // is the number "store_matches"

	// a percentage?
	public static final int MAX_RESULT_PAIRS = 1000000;

	public String result_dir = "result";

	public String helper[] = new String[1];

	public static final int COMPMODE_NORMAL = 0;
	public static final int COMPMODE_REVISION = 1;

	public int comparisonMode = COMPMODE_NORMAL;

	public int min_token_match;

	public String[] suffixes;

	public boolean exp = false; // EXPERIMENT

	public boolean diff_report = false; // special "diff" report

	public jplag.filter.Filter filter = null;

	public String filtername = "."; // == no filter

	public String basecode = "";

	public String archivalSubmissions = "";

	public String commandLine = "";

	// "Ronald Kostoff" specials
	public boolean externalSearch = false;

	public boolean skipParse = false;

	public boolean clustering = false;

	public boolean useBasecode = false;

	public boolean useArchivalSubmissions = false;

	public String languageName = null;

	public String countryTag = "en";

	public float[] threshold = null;

	public int[] themewords = { 15 };

	public int clusterType = 0;

	public int compare = 0; // 0 = deactivated

	public SimilarityMatrix similarity = null;

        public boolean ignoreSuffixes = false;

	public jplag.Language language;

	//compare list of files options
	public boolean fileListMode = false;

	public List<String> fileList = new ArrayList<String>();

	public boolean showProgress = false;

	public String progressToken = "";

	// "FINAL" OPTIONS
	public boolean debugParser = false;

	// END OPTIONS

	public static final int PARSING = 100;
	public static final int PARSING_WARNINGS = 101;
	public static final int COMPARING = 200;
	public static final int GENERATING_RESULT_FILES = 250;
	public static final int SUBMISSION_ABORTED = 405;

	private int int_progress = 0;

	private int state = 50;

	private boolean forceStop = false;

	public void forceProgramToStop() {
		forceStop = true;
	}

	public boolean isForceStop() {
		return forceStop;
	}

	public int getProgress() {
		return int_progress;
	}

	public int getState() {
		return state;
	}

	public void setProgress(int progress) throws jplag.ExitException {
		int_progress = progress;
		if (forceStop)
			throw new jplag.ExitException("Submission aborted",
					SUBMISSION_ABORTED);
	}

	public void setState(int state) {
		this.state = state;
	}

	public abstract void initializeSecondStep(Program program)
			throws jplag.ExitException;

	// TODO control how the exclusion file is handled by the Program

	public static void usage() {
		System.out.print(Program.name_long
						+ ", Copyright (c) 2004-2017 KIT - IPD Tichy, Guido Malpohl, and others.\n"
						+ "Usage: JPlag [ options ] <root-dir> [-c file1 file2 ...]\n"
						+ " <root-dir>      The root-directory that contains all submissions\n\n"
						+ "options are:\n"
						+ " -v[qlpd]        (Verbose)\n"
						+ "                 q: (Quiet) no output\n"
						+ "                 l: (Long) detailed output\n"
						+ "                 p: print all (p)arser messages\n"
						+ "                 d: print (d)etails about each submission\n"
						+ " -d              (Debug) parser. Non-parsable files will be stored.\n"
						+ " -S <dir>        Look in directories <root-dir>/*/<dir> for programs.\n"
						+ "                 (default: <root-dir>/*)\n"
						+ " -s              (Subdirs) Look at files in subdirs too (default: deactivated)\n\n"
						+ " -p <suffixes>   <suffixes> is a comma-separated list of all filename suffixes\n"
						+ "                 that are included. (\"-p ?\" for defaults)\n\n"
						+ " -o <file>       (Output) The Parserlog will be saved to <file>\n"
						+ " -x <file>       (eXclude) All files named in <file> will be ignored\n"
						+ " -t <n>          (Token) Tune the sensitivity of the comparison. A smaller\n"
						+ "                 <n> increases the sensitivity.\n"
						+ " -m <n>          (Matches) Number of matches that will be saved (default:20)\n"
						+ " -m <p>%         All matches with more than <p>% similarity will be saved.\n"
						+ " -r <dir>        (Result) Name of directory in which the web pages will be\n"
						+ "                 stored (default: result)\n"
						+ " -a <dir>        (Archival) Directory containing archived submissions, which\n"
						+ "                 will be only compared against\n"
						+ " -bc <dir>       Directory which contains the basecode (common framework)\n"
						+ " -c [files]      Compare a list of files. Should be the last one.\n"
						+ " -l <language>   (Language) Supported Languages:\n                 ");
		for (int i = 0; i < languages.length - 2; i += 2)
			System.out.print(languages[i] + (i == 0 ? " (default), " : ", "));
		System.out.println(languages[languages.length - 2]);
	}

	protected static void printAllLanguages() throws jplag.ExitException {
		for (int i = 0; i < languages.length - 1; i += 2)
			try {
				Language langClass = (Language) Class.forName(languages[i + 1])
						.newInstance();
				System.out.println(languages[i]);
				String suffixes[] = langClass.suffixes();
				for (int j = 0; j < suffixes.length; j++)
					System.out.print(suffixes[j]
							+ (j + 1 < suffixes.length ? "," : "\n"));
				System.out.println(langClass.min_token_match());
			} catch (ClassNotFoundException e) {
			} catch (IllegalAccessException e) {
			} catch (InstantiationException e) {
			}
		throw new jplag.ExitException("printAllLanguages exited");
	}

	public String getClusterTyp() {
		if (this.clusterType == MIN_CLUSTER)
			return "min";
		else if (this.clusterType == MAX_CLUSTER)
			return "max";
		else if (this.clusterType == AVR_CLUSTER)
			return "avr";
		else
			return "";
	}

	public String getCountryTag() {
		return this.countryTag;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
