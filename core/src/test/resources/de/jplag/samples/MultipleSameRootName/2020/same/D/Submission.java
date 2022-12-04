package samples.PartialPlagiarism.D;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Vector;

/*
 * Everything about a single submission is stored in this object. (directory,
 * files, ...)
 */
public class Submission implements Comparable<Submission> {
	public String name;

	private boolean readSubDirs;

	public File dir;

	public String[] files = null; // = new String[0];

	public Structure structure;

	public int structSize = 0;

	// public long structMem;
	boolean exact_match = false; // used for fallback

	public boolean errors = false;

	public DecimalFormat format = new DecimalFormat("0000");

	public Submission(String name, File dir, boolean readSubDirs) {
		this.dir = dir;
		this.name = name;
		this.readSubDirs = readSubDirs;
		try {
			lookupDir(dir, "");
		} catch (Throwable b) {
		}
	}

	public Submission(String name, File dir) {
		this.dir = dir;
		this.name = name;
		this.readSubDirs = false;

		files = new String[1];
		files[0] = name;

	}

	// recursively read in all the files
	private void lookupDir(File dir, String subDir) throws Throwable {
		File aktDir = new File(dir, subDir);
		if (!aktDir.isDirectory())
			return;
		if (readSubDirs) {
			String[] dirs = aktDir.list();
			if (!subDir.equals(""))
				for (int i = 0; i < dirs.length; i++)
					lookupDir(dir, subDir + File.separator + dirs[i]);
			else
				for (int i = 0; i < dirs.length; i++)
					lookupDir(dir, dirs[i]);
		}
		String[] newFiles = aktDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
					return false;
			}
		});
		if (files != null) {
			String[] oldFiles = files;
			files = new String[oldFiles.length + newFiles.length];
			if (subDir != "")
				for (int i = 0; i < newFiles.length; i++)
					files[i] = subDir + File.separator + newFiles[i];
			else
				System.arraycopy(newFiles, 0, files, 0, newFiles.length);
			System.arraycopy(oldFiles, 0, files, newFiles.length, oldFiles.length);
		} else {
			if (subDir != "") {
				files = new String[newFiles.length];
				for (int i = 0; i < newFiles.length; i++)
					files[i] = subDir + File.separator + newFiles[i];
			} else
				files = newFiles;
		}
	}

	/* parse all the files... */
	public boolean parse(){
		return false;
	}

	/*
	 * This method is used to copy files that can not be parsed to a special
	 * folder: de/jplag/errors/java old_java scheme cpp /001/(...files...)
	 * /002/(...files...)
	 */
	private void doSmth() {
		File failureDir = null;
		try {
			URL url = Submission.class.getResource("errors");
			failureDir = new File(url.getFile());
		} catch (NullPointerException e) {
			return;
		}

		if (!failureDir.exists())
			failureDir.mkdir();
		int i = 0;
	}

	private void copyFile(File in, File out) {
		byte[] buffer = new byte[10000];
		try {
			FileInputStream des = new FileInputStream(in);
			FileOutputStream foo = new FileOutputStream(out);
			int count;
			do {
				count = des.read(buffer);
				if (count != -1)
					foo.write(buffer, 0, count);
			} while (count != -1);
			des.close();
			foo.close();
		} catch (IOException e) {
			System.out.println("Oh no!");
		}
	}

	public int size() {
		if (structure != null)
			return structSize = structure.size();
		return structSize;
	}

	/*
	 * Used by the "Report" class. All source files are returned as an array of
	 * an array of strings.
	 */
	public String[][] readFiles(String[] files) {
		String[][] newName = new String[files.length][];
		String help;

		Vector<String> text = new Vector<String>();
		for (int i = 0; i < files.length; i++) {
			text.removeAllElements();
			try {
				/* file encoding = "UTF-8" */
				FileInputStream fileInputStream = new FileInputStream(new File(dir, files[i]));
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
				BufferedReader in = new BufferedReader(inputStreamReader);
				while ((help = in.readLine()) != null) {
					help = help.replaceAll("&", "&amp;");
					text.addElement(help);
				}
				in.close();
				inputStreamReader.close();
				fileInputStream.close();
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + ((new File(dir, files[i])).toString()));
			} catch (IOException e) {
				System.out.println("Oh no!");
			}
			newName[i] = new String[text.size()];
			text.copyInto(newName[i]);
		}
		return newName;
	}

	/*
	 * Used by the "Report" class. All source files are returned as an array of
	 * an array of chars.
	 */
	public char[][] readFilesChar(String[] files) {
		char[][] variabl = new char[files.length][];

		for (int i = 0; i < files.length; i++) {
			try {
				File file = new File(dir, files[i]);
				int size = (int) file.length();
				char[] buffer = new char[size];

				FileReader fis = new FileReader(file);

				if (size != fis.read(buffer)) {
					System.out.println("Not right size read from the file, " + "but I will still continue...");
				}

				variabl[i] = buffer;
				fis.close();
			} catch (FileNotFoundException e) {
				System.out.println("Oh no");
			} catch (IOException e) {

			}
		}
		return variabl;
	}

	@Override
	public int compareTo(Submission o) {
		return 0;
	}
}
