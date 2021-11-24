package de.jplag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.jplag.options.JPlagOptions;

/**
 * Represents a single submission. A submission can contain multiple files.
 */
public class Submission implements Comparable<Submission> {
    /**
     * Directory name for storing submission files with parse errors if so requested.
     */
    private static final String ERROR_FOLDER = "errors";

    /**
     * Identification of the submission (often a directory or file name).
     */
    private final String name;

    /**
     * Root of the submission (either a file or a directory).
     */
    private final File submissionRoot;

    /**
     * Files of the submission.
     */
    private final Collection<File> files;

    /**
     * Back-link to the main application.
     */
    private final JPlag program;

    /**
     * Whether an error occurred during parsing the submission files.
     */
    private boolean hasErrors;

    /**
     * Parse result, tokens from all files.
     */
    private TokenList tokenList;

     /**
     * baseCodeMatche
     */
    private JPlagComparison baseCodeMatch;

    /**
     * @param name Identification of the submission (directory or filename).
     * @param submissionRoot Root of the submission (either a file or a directory).
     * @param program Back-link to the main application.
     */
    public Submission(String name, File submissionRoot, JPlag program) {
        this.name = name;
        this.submissionRoot = submissionRoot;
        this.program = program;
        this.files = parseFilesRecursively(submissionRoot);
    }

    /**
     * @return a list of files this submission consists of.
     */
    public Collection<File> getFiles() {
        return files;
    }

    /**
     * @return Identification of the submission (often a directory or file name).
     */
    public String getName() {
        return name;
    }

    /**
     * @return Number of tokens in the parse result.
     */
    public int getNumberOfTokens() {
        if (tokenList == null) {
            return 0;
        }

        return tokenList.size();
    }

   /**
     * Sets the base code match
     * @param baseCodeMatch is submissions matches with the base code
     */
    public void setBaseCodeMatch(JPlagComparison baseCodeMatch) {
        this.baseCodeMatch = baseCodeMatch;
    }

    /**
     * @return Number of tokens in the parse result.
     */
    public JPlagComparison getBaseCodeMatch() {
        return baseCodeMatch;
    }

    /**
     * @return Parse result of the submission.
     */
    public TokenList getTokenList() {
        return tokenList;
    }

    /**
     * @return true if at least one error occurred while parsing this submission; false otherwise.
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * Parse files of the submission.
     * @return Whether parsing was successful.
     */
    public boolean parse() {
        if (files == null || files.size() == 0) {
            program.print("ERROR: nothing to parse for submission \"" + name + "\"\n", null);
            return false;
        }

        String[] relativeFilePaths = getRelativeFilePaths(submissionRoot, files);

        tokenList = this.program.getLanguage().parse(submissionRoot, relativeFilePaths);
        if (!program.getLanguage().hasErrors()) {
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
    public String[][] readFiles(String[] files) throws de.jplag.ExitException {
        String[][] result = new String[files.length][];
        String help;
        ArrayList<String> text = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            text.clear();

            try {
                FileInputStream fileInputStream = new FileInputStream(new File(submissionRoot, files[i]));
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, JPlagOptions.CHARSET);
                BufferedReader in = new BufferedReader(inputStreamReader);

                while ((help = in.readLine()) != null) {
                    help = help.replaceAll("&", "&amp;");
                    help = help.replaceAll("<", "&lt;");
                    help = help.replaceAll(">", "&gt;");
                    help = help.replaceAll("\"", "&quot;");
                    text.add(help);
                }

                in.close();
                inputStreamReader.close();
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + ((new File(submissionRoot, files[i])).toString()));
            } catch (IOException e) {
                throw new de.jplag.ExitException("I/O exception!");
            }

            result[i] = new String[text.size()];
            text.toArray(result[i]);
        }

        return result;
    }

    /**
     * Used by the "Report" class. All source files are returned as an array of an array of chars.
     */
    public char[][] readFilesChar(String[] files) throws de.jplag.ExitException {
        char[][] result = new char[files.length][];

        for (int i = 0; i < files.length; i++) {
            // If the token path is absolute, ignore the provided directory
            File file = new File(files[i]);
            if (!file.isAbsolute()) {
                file = new File(submissionRoot, files[i]);
            }

            try {
                int size = (int) file.length();
                char[] buffer = new char[size];

                FileReader reader = new FileReader(file, JPlagOptions.CHARSET);

                if (size != reader.read(buffer)) {
                    System.out.println("Not right size read from the file, " + "but I will still continue...");
                }

                result[i] = buffer;
                reader.close();
            } catch (FileNotFoundException e) {
                // TODO PB: Should an ExitException be thrown here?
                System.out.println("File not found: " + file.getPath());
            } catch (IOException e) {
                throw new de.jplag.ExitException("I/O exception reading file \"" + file.getPath() + "\"!", e);
            }
        }

        return result;
    }

    /**
     * Resets the base code flag for all tokens of this submission.
     */
    public void resetBaseCode() {
        for (Token token : tokenList.allTokens()) {
            token.basecode = false;
        }
    }

    /**
     * Sets the tokens that have been parsed from the files this submission consists of.
     * @param tokenList is the list of these tokens.
     */
    public void setTokenList(TokenList tokenList) {
        this.tokenList = tokenList;
    }

    @Override
    public int compareTo(Submission other) {
        return name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name;
    }

    /** Physical copy. :-) */
    private void copyFile(File in, File out) {
        byte[] buffer = new byte[10000];
        try {
            FileInputStream input = new FileInputStream(in);
            FileOutputStream output = new FileOutputStream(out);
            int count;
            do {
                count = input.read(buffer);
                if (count != -1) {
                    output.write(buffer, 0, count);
                }
            } while (count != -1);
            input.close();
            output.close();
        } catch (IOException e) {
            program.print("Error copying file: " + e.toString() + "\n", null);
        }
    }

    /*
     * This method is used to copy files that can not be parsed to a special folder: de/jplag/errors/java old_java scheme cpp
     * /001/(...files...) /002/(...files...)
     */
    private void copySubmission() {
        File errorDir = null;
        DecimalFormat format = new DecimalFormat("0000");

        try {
            URL url = Submission.class.getResource(ERROR_FOLDER);
            errorDir = new File(url.getFile());
        } catch (NullPointerException e) {
            return;
        }

        errorDir = new File(errorDir, this.program.getLanguage().getShortName());

        if (!errorDir.exists()) {
            errorDir.mkdir();
        }

        int i = 0;
        File destDir;

        while ((destDir = new File(errorDir, format.format(i))).exists()) {
            i++;
        }

        destDir.mkdir();

        for (File file : files) {
            copyFile(new File(file.getAbsolutePath()), new File(destDir, file.getName()));
        }
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
    private String[] getRelativeFilePaths(File baseFile, Collection<File> files) {
        Path baseFilePath = baseFile.toPath();

        return files.stream().map(File::toPath).map(baseFilePath::relativize).map(Path::toString).toArray(String[]::new);
    }

    /**
     * Recursively scan the given directory for nested files. Excluded files and files with an invalid suffix are ignored.
     * <p>
     * If the given file is not a directory, the input will be returned as a singleton list.
     * @param file - File to start the scan from.
     * @return a list of nested files.
     */
    private Collection<File> parseFilesRecursively(File file) {
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

        Collection<File> files = new ArrayList<>();

        for (String fileName : nestedFileNames) {
            files.addAll(parseFilesRecursively(new File(file, fileName)));
        }

        return files;
    }

}
