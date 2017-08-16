package jplag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Vector;

/*
 * Everything about a single submission is stored in this object. (directory,
 * files, ...)
 */
public class Submission implements Comparable<Submission> {
	public String name;

	private Program program;

	private boolean readSubDirs;

	private Language language;

	public File dir;

	public String[] files = null; // = new String[0];

	public Structure struct;

	public int structSize = 0;

	// public long structMem;
	boolean exact_match = false; // used for fallback

	public boolean errors = false;

	public DecimalFormat format = new DecimalFormat("0000");

	public Submission(String name, File dir, boolean readSubDirs, Program p, Language language) {
		this.program = p;
		this.language = language;
		this.dir = dir;
		this.name = name;
		this.readSubDirs = readSubDirs;
		try {
			lookupDir(dir, "");
		} catch (Throwable b) {
		}
		if (program.use_verbose_details()) {
			program.print("Files in submission '" + name + "':\n", null);
			for (int i = 0; i < files.length; i++)
				program.print("  " + files[i] + '\n', null);
		}
	}

	public Submission(String name, File dir, Program p, Language language) {
		this.language = language;
		this.program = p;
		this.dir = dir;
		this.name = name;
		this.readSubDirs = false;

		files = new String[1];
		files[0] = name;

		if (program.use_verbose_details()) {
			program.print("Files in submission '" + name + "':\n", null);
			for (int i = 0; i < files.length; i++)
				program.print("  " + files[i] + '\n', null);
		}
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
				if (!new File(dir, name).isFile())
					return false;
				if (program.excludeFile(name))
					return false;
				String[] suffies = program.get_suffixes();
				for (int i = 0; i < suffies.length; i++)
					if (exact_match) {
						if (name.equals(suffies[i]))
							return true;
					} else {
						if (name.endsWith(suffies[i]))
							return true;
					}
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
	public boolean parse() throws jplag.ExitException {
		if (!program.use_verbose_parser()) {
			if (files == null || files.length == 0) {
				program.print("ERROR: nothing to parse for submission \"" + name + "\"\n", null);
				return false;
			}
		}

		struct = this.language.parse(dir, files);
		if (!language.errors()) {
			if (struct.size() < 3) {
				program.print("Submission \"" + name + "\" is too short!\n", null);
				struct = null;
				errors = true; // invalidate submission
				return false;
			}
			return true;
		}

		struct = null;
		errors = true; // invalidate submission
		if (program.use_debugParser())
			copySubmission();
		return false;
	}

	/*
	 * This method is used to copy files that can not be parsed to a special
	 * folder: jplag/errors/java old_java scheme cpp /001/(...files...)
	 * /002/(...files...)
	 */
	private void copySubmission() {
		File errorDir = null;
		try {
			URL url = Submission.class.getResource("errors");
			errorDir = new File(url.getFile());
		} catch (NullPointerException e) {
			return;
		}
		errorDir = new File(errorDir, this.language.getShortName());
		if (!errorDir.exists())
			errorDir.mkdir();
		int i = 0;
		File destDir;
		while ((destDir = new File(errorDir, format.format(i))).exists())
			i++;
		destDir.mkdir();
		for (i = 0; i < files.length; i++)
			copyFile(new File(dir, files[i]), new File(destDir, files[i]));
	}

	/* Physical copy. :-) */
	private void copyFile(File in, File out) {
		byte[] buffer = new byte[10000];
		try {
			FileInputStream dis = new FileInputStream(in);
			FileOutputStream dos = new FileOutputStream(out);
			int count;
			do {
				count = dis.read(buffer);
				if (count != -1)
					dos.write(buffer, 0, count);
			} while (count != -1);
			dis.close();
			dos.close();
		} catch (IOException e) {
			program.print("Error copying file: " + e.toString() + "\n", null);
		}
	}

	public int size() {
		if (struct != null)
			return structSize = struct.size();
		return structSize;
	}

	/*
	 * Used by the "Report" class. All source files are returned as an array of
	 * an array of strings.
	 */
	public String[][] readFiles(String[] files) throws jplag.ExitException {
		String[][] result = new String[files.length][];
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
					help = help.replaceAll("<", "&lt;");
					help = help.replaceAll(">", "&gt;");
					help = help.replaceAll("\"", "&quot;");
					text.addElement(help);
				}
				in.close();
				inputStreamReader.close();
				fileInputStream.close();
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + ((new File(dir, files[i])).toString()));
			} catch (IOException e) {
				throw new jplag.ExitException("I/O exception!");
			}
			result[i] = new String[text.size()];
			text.copyInto(result[i]);
		}
		return result;
	}

	/*
	 * Used by the "Report" class. All source files are returned as an array of
	 * an array of chars.
	 */
	public char[][] readFilesChar(String[] files) throws jplag.ExitException {
		char[][] result = new char[files.length][];

		for (int i = 0; i < files.length; i++) {
			try {
				File file = new File(dir, files[i]);
				int size = (int) file.length();
				char[] buffer = new char[size];

				FileReader fis = new FileReader(file);

				if (size != fis.read(buffer)) {
					System.out.println("Not right size read from the file, " + "but I will still continue...");
				}

				result[i] = buffer;
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO: Should an ExitException be thrown here?
				System.out.println("File not found: " + ((new File(dir, files[i])).toString()));
			} catch (IOException e) {
				throw new jplag.ExitException("I/O exception reading file \"" + (new File(dir, files[i])).toString() + "\"!", e);
			}
		}
		return result;
	}

	public int compareTo(Submission other) {
		return name.compareTo(other.name);
	}

	public String toString() {
		return name;
	}
}
