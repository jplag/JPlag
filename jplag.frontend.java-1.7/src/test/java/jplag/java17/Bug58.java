package jplag.java17;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

/** See https://svn.ipd.kit.edu/trac/jplag/ticket/58 */
public class Bug58 {
	private static File srcTestResources;

	@BeforeClass
	public static void getPaths() {
		srcTestResources = new File(System.getProperty("user.dir"), "src/test/resources");
	}

	@Test
	public void parseEmptyFile() throws IOException {
		parseWithActualParser(new File(srcTestResources, "EmptyFile.java"));
	}

	@Test
	public void parseEmptyFileOnlyComments() throws IOException {
		parseWithActualParser(new File(srcTestResources, "EmptyFileOnlyComments.java"));
	}

	@Test
	public void parseEmptyFileOnlyOneComment() throws IOException {
		parseWithActualParser(new File(srcTestResources, "EmptyFileOneComment.java"));
	}

	/**
	 * Uses the actual parser component (not only the antlr parser but the jplag
	 * frontend) to parse a file. Should throw no errors and exceptions :)
	 * 
	 * @param file
	 *            file to parse
	 */
	private void parseWithActualParser(File file) {
		Parser parser = new Parser();
		parser.setProgram(new jplag.StrippedProgram());
		parser.parse(null, new String[] { file.toString() });
	}
}
