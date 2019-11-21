package jplag.cpp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jplag.Parser;
import jplag.StreamParser;
import jplag.StrippedProgram;
import jplag.TokenAdder;
import org.jetbrains.annotations.NotNull;

public class Scanner extends StreamParser implements CPPTokenConstants {
	public static void main(String args[]) {
		System.out.print("File: ");
		for (int i = 0; i < args.length; i++) {
			System.out.print(args[i] + " ");
		}
		System.out.println();

		Scanner scanner = new Scanner();
		scanner.setProgram(new StrippedProgram());

		scanner.parse(new File("."), args);
	}

	@NotNull
	@Override
	public jplag.Token getEndOfFileToken(String file) {
		return new CPPToken(FILE_END, file, 1);
	}

	@Override
	public boolean parseStream(@NotNull InputStream stream, @NotNull TokenAdder adder) throws IOException {
		CPPScanner scanner = null;// will be initialized in Method scanFile

		return CPPScanner.scanStream(
				stream,
				scanner,
				adder.currentFile,
				new CPPTokenCreator(adder),
				this.getProgram()
		);
	}
}
