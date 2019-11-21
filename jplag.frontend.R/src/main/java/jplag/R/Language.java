package jplag.R;

import java.io.File;

import jplag.ProgramI;


/*
En este archivo definimos lo que JPLAG tiene que saber del lenguaje cuyo frontend estamos haciendo: los sufijos de los archivos de este lenguaje, nombre que le ponemos al parser,
el número mínimo de tokens iguales que se considere un emparejamiento,...
*/

public class Language implements jplag.Language {
	private Parser parser;

	public Language(ProgramI program) {
		this.parser = new Parser();
		this.parser.setProgram(program);

	}

	public String[] suffixes() {
		String[] res = { ".r", ".R" };
		return res;
	}

	public int errorsCount() {
		// TODO Auto-generated method stub
		return this.parser.errorsCount();
	}

	public String name() {
		return "R Parser";
	}

	public String getShortName() {
		return "R";
	}

	public int min_token_match() {
		return 8;
	}

	public jplag.Structure parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	public boolean errors() {
		return parser.getErrors();
	}

	public boolean supportsColumns() {
		return true;
	}

	public boolean isPreformated() {
		return true;
	}

	public boolean usesIndex() {
		return false;
	}

	public int noOfTokens() {
		return jplag.R.RToken.staticNumberOfTokens();
	}

	public String type2string(int type) {
		return jplag.R.RToken.type2string(type);
	}
}
