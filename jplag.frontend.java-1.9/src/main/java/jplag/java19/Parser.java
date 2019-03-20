package jplag.java19;

import java.io.File;

import jplag.java19.JavaToken;

public class Parser  extends jplag.Parser {
	private jplag.Structure struct;

	public jplag.Structure parse(File dir, String files[]) {
		struct = new jplag.Structure();
		errors = 0;
		File pathedFiles[] = new File[files.length];
		for (int i = 0; i < files.length; i++) {
			pathedFiles[i]=new File(dir,files[i]); 
		}
		JavacAdapter javac = new JavacAdapter();
		errors += javac.parseFiles(dir,pathedFiles,this);
		
		
		this.parseEnd();
		return struct;
	}
	
	public void add(int type,String filename, long line, long col, long length) {
		struct.addToken(new JavaToken(type, filename, (int) line, (int)col,(int)length));
	}

	public void errorsInc() {
		errors++;
	}
}
