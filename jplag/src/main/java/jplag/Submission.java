package jplag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import jplag.options.Verbosity;

/**
 * Represents a single submission. A submission can contain multiple files.
 */
public class Submission implements Comparable<Submission> {

    /**
     * Name that uniquely identifies this submission. Will most commonly be the directory or file name.
     */
    public String name;

    public File submissionFile;

    /**
     * List of files this submission consists of.
     */
    public List<File> files;

    /**
     * List of tokens that have been parsed from the files this submission consists of.
     * <p>
     * TODO PB: The name 'Structure' is very generic and should be changed to something more descriptive.
     */
    public Structure tokenList;

    /**
     * True, if at least one error occurred while parsing this submission; false otherwise.
     */
    public boolean hasErrors = false;

    private final JPlag program;

    public Submission(String name, File submissionFile, JPlag program) {
        this.name = name;
        this.submissionFile = submissionFile;
        this.program = program;
        this.files = parseFilesRecursively(submissionFile);
    }

    /**
     * Recursively scan the given directory for nested files. Excluded files and files with an invalid suffix are ignored.
     * <p>
     * If the given file is not a directory, the input will be returned as a singleton list.
     * @param file - File to start the scan from.
     * @return a list of nested files.
     */
    private List<File> parseFilesRecursively(File file) {
        if (program.isFileExcluded(file)) {
            return Collections.emptyList();
        }

        if (file.isFile() && program.hasValidSuffix(file)) {
            return Collections.singletonList(file);
        }

        String[] nestedFileNames = file.list();

        if (nestedFileNames == null) {
            return Collections.emptyList();
        }

        List<File> files = new ArrayList<>();

        for (String fileName : nestedFileNames) {
            files.addAll(parseFilesRecursively(new File(file, fileName)));
        }

        return files;
    }

    public int getNumberOfTokens() {
        if (tokenList == null) {
            return 0;
        }

        return tokenList.size();
    }

    @Override
    public int compareTo(Submission other) {
        return name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Map all files of this submission to their path relative to the submission directory.
     * <p>
     * This method is required to stay compatible with `program.language.parse(...)` as it requires the given file paths to
     * be relative to the submission directory.
     * <p>
     * In a future update, `program.language.parse(...)` should probably just take a list of files.
     * @param baseFile - File to base all relative file paths on.
     * @param files - List of files to map.
     * @return an array of file paths relative to the submission directory.
     */
    public String[] getRelativeFilePaths(File baseFile, List<File> files) {
        Path baseFilePath = baseFile.toPath();

        return files.stream().map(File::toPath).map(baseFilePath::relativize).map(Path::toString).toArray(String[]::new);
    }

    /* parse all the files... */
    public boolean parse() {
        if (program.getOptions().getVerbosity() != Verbosity.PARSER) {
            if (files == null || files.size() == 0) {
                program.print("ERROR: nothing to parse for submission \"" + name + "\"\n", null);
                return false;
            }
        }

        String[] relativeFilePaths = getRelativeFilePaths(submissionFile, files);

        tokenList = this.program.language.parse(submissionFile, relativeFilePaths);
        if (!program.language.errors()) {
            if (tokenList.size() < 3) {
                program.print("Submission \"" + name + "\" is too short!\n", null);
                tokenList = null;
                hasErrors = true; // invalidate submission
                return false;
            }
            return true;
        }

        tokenList = null;
        hasErrors = true; // invalidate submission
        if (program.getOptions().isDebugParser()) {
            copySubmission();
        }
        return false;
    }

    /**
     * Used by the "Report" class. All source files are returned as an array of an array of strings.
     */
    public String[][] readFiles(String[] files) throws jplag.ExitException {
        String[][] result = new String[files.length][];
        String help;
        Vector<String> text = new Vector<>();

        for (int i = 0; i < files.length; i++) {
            text.removeAllElements();

            try {
                /* file encoding = "UTF-8" */
                FileInputStream fileInputStream = new FileInputStream(new File(submissionFile, files[i]));
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
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
                System.out.println("File not found: " + ((new File(submissionFile, files[i])).toString()));
            } catch (IOException e) {
                throw new jplag.ExitException("I/O exception!");
            }

            result[i] = new String[text.size()];
            text.copyInto(result[i]);
        }

        return result;
    }

    /**
     * Used by the "Report" class. All source files are returned as an array of an array of chars.
     */
    public char[][] readFilesChar(String[] files) throws jplag.ExitException {
        char[][] result = new char[files.length][];

        for (int i = 0; i < files.length; i++) {
            // If the token path is absolute, ignore the provided directory
            File file = new File(files[i]);
            if (!file.isAbsolute()) {
                file = new File(submissionFile, files[i]);
            }
            
            try {
                int size = (int) file.length();
                char[] buffer = new char[size];

                FileReader reader = new FileReader(file);

                if (size != reader.read(buffer)) {
                    System.out.println("Not right size read from the file, " + "but I will still continue...");
                }

                result[i] = buffer;
                reader.close();
            } catch (FileNotFoundException e) {
                // TODO PB: Should an ExitException be thrown here?
                System.out.println("File not found: " + file.getPath());
            } catch (IOException e) {
                throw new jplag.ExitException("I/O exception reading file \"" + file.getPath() + "\"!", e);
            }
        }

        return result;
    }

    /*
     * This method is used to copy files that can not be parsed to a special folder: jplag/errors/java old_java scheme cpp
     * /001/(...files...) /002/(...files...)
     */
    private void copySubmission() {
        File errorDir = null;
        DecimalFormat format = new DecimalFormat("0000");

        try {
            URL url = Submission.class.getResource("errors");
            errorDir = new File(url.getFile());
        } catch (NullPointerException e) {
            return;
        }

        errorDir = new File(errorDir, this.program.language.getShortName());

        if (!errorDir.exists()) {
            errorDir.mkdir();
        }

        int i = 0;
        File destDir;

        while ((destDir = new File(errorDir, format.format(i))).exists()) {
            i++;
        }

        destDir.mkdir();

        for (i = 0; i < files.size(); i++) {
            copyFile(new File(files.get(i).getAbsolutePath()), new File(destDir, files.get(i).getName()));
        }
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
                if (count != -1) {
                    dos.write(buffer, 0, count);
                }
            } while (count != -1);
            dis.close();
            dos.close();
        } catch (IOException e) {
            program.print("Error copying file: " + e.toString() + "\n", null);
        }
    }
}
