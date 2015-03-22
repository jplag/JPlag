/*
 * Created on Jun 1, 2005
 */
package atujplag.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

/*
 * This is basically the same class that is used in the actual JPlag
 * application.
 */
public class SubmissionManager {
    public static final int VALID = 0;
    public static final int NOFILES = 1;
    public static final int FILENOTINSUBDIR = 2;
    public static final int DIRHASNOTSUBDIR = 3;
    
	public String name;

	public File dir;

	public String[] files = new String[0];

	private boolean readSubDirs = false;

	private String[] suffixes;

	// TODO: Do we really need this?
	private static final boolean EXACT_MATCH = false;

	private boolean isdir = false;

	private int errorCode = VALID;

	public SubmissionManager(String name, File dir, boolean readSubDirs,
			String[] suffixes) throws InterruptedException {
		this.name = name;
		this.dir = dir;
		this.readSubDirs = readSubDirs;
		this.suffixes = suffixes;
		this.isdir = true;
		lookupDir(dir, "");
		if (this.files.length == 0)
			this.errorCode = NOFILES;
	}

	public SubmissionManager(String name, File dir) {
		this.dir = dir;
		this.name = name;
		files = new String[1];
		files[0] = name;
	}
    
    public SubmissionManager(String name, int error) {
        dir = null;
        this.name = name;
        errorCode = error;
        if(errorCode == DIRHASNOTSUBDIR || errorCode == NOFILES) isdir = true;
    }

	public boolean isDirectory() {
		return isdir;
	}

    public void collectFiles(Vector<File> fileVector) {
        for(int i=0; i<files.length; i++)
            fileVector.add(new File(dir, files[i]));
    }

	// scan the whole directory
	private void lookupDir(File dir, String subDir) throws InterruptedException {
		File aktDir = new File(dir, subDir);

		if (Thread.currentThread().isInterrupted())
			throw new InterruptedException();

		if (readSubDirs) {
			String[] dirs = aktDir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (new File(dir, name).isDirectory()); // all sub-dirs
				}
			});
			if (dirs != null)
				if (subDir != "")
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
				for (int i = 0; i < suffixes.length; i++)
					if (!EXACT_MATCH) {
						if (name.endsWith(suffixes[i]))
							return true;
					} else {
						if (name.equals(suffixes[i]))
							return true;
					}
				return false;
			}
		});
		String[] oldFiles = files;
		files = new String[((oldFiles != null ? oldFiles.length : 0)
				+ (newFiles != null ? newFiles.length : 0))];
		if (newFiles != null) {
			if (subDir != "" && newFiles != null)
				for (int i = 0; i < newFiles.length; i++)
					files[i] = subDir + File.separator + newFiles[i];
			else
				System.arraycopy(newFiles, 0, files, 0, newFiles.length);
			if (oldFiles != null)
				System.arraycopy(oldFiles, 0, files, newFiles.length,
						oldFiles.length);
		}
	}

	public File getDir() {
		return this.dir;
	}

	/**
	 * @return Returns whether this submission is valid
	 */
	public boolean isValid() {
		return errorCode == VALID;
	}
    
    public String getErrorString() {
        String msg;
        switch(errorCode) {
            case NOFILES:
                msg = "SubmissionManager.No_files";
                break;
            case FILENOTINSUBDIR:
                msg = "SubmissionManager.File_not_in_subdir";
                break;
            case DIRHASNOTSUBDIR:
                msg = "SubmissionManager.Directory_does_not_contain_subdir";
                break;
            default:
                msg = "SubmissionManager.Unknown_error_code";
                break;
        }
        return Messages.getString(msg);
    }
}