package jplag;


import java.io.*;

/**
 * One interface for all languages...
 */
public interface Language {
	/** Get known suffixes for files containing code in the language. */
	public String[] suffixes();

	/** Descriptive name of the language (one line). */
	public String name();

	/** Short (one word) name of the language. */
	public String getShortName();

	/** Minimum number of tokens needed for a match. */
	public int min_token_match();

	/** Parse a set files in a directory, and return the parse result. */
	public Structure parse(File dir, String[] files);

	/** Whether errors were found during the last {@link #parse}. */
	public boolean errors();

	/** Number of found errors found during the last {@link #parse}. */
	public int errorsCount();

	/** Does the parser provide column information? */
	public boolean supportsColumns();

	/** Whether JPlag should use a fixed-width font in its reports. */
	public boolean isPreformated();

	/** Whether tokens from the scanner are indexed. */
	public boolean usesIndex();

	/** Number of defined tokens in the scanner of the language. */
	public int noOfTokens();

	/** Convert a token type to a text representation. */
	public String type2string(int type);
}
