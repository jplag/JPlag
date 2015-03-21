package jplag;


import java.io.*;

/**
 * One interface for all languages...
 */
public interface Language {
	public String[] suffixes();

	public String name();

	public String getShortName();

	public int min_token_match();

	public Structure parse(File dir, String[] files);

	public boolean errors();

	public int errorsCount();

	public boolean supportsColumns();

	public boolean isPreformated();

	public boolean usesIndex();

	public int noOfTokens();

	public String type2string(int type);
}
