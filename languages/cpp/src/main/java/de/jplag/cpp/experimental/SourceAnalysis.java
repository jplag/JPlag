package de.jplag.cpp.experimental;

import java.io.File;
import java.util.Set;

/**
 * Strategy for analyzing source code for unused variables querying the corresponding lines
 */
public interface SourceAnalysis {

    /**
     * Tells the caller if a token is located in a line containing an unused variable. This usually indicates that the token
     * belongs to a declaration of an unused variable. An edge case is multiple variable declarations in a single line, e.g.
     * 'int a, b;' where a is used an b is unused.
     * @param token The token that will be checked
     * @param file The file the token was scanned in
     * @return True, if the token should not be added to a TokenList, false if it should
     */
    boolean isTokenIgnored(de.jplag.cpp.Token token, File file);

    /**
     * Executes the source analysis on the files of a submission.
     * @param files Set of the files contained in the submission
     */
    void findUnusedVariableLines(Set<File> files) throws InterruptedException;
}
