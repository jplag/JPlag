package de.jplag.cli;

/**
 * The mode JPlag runs in. This influences which steps JPlag will execute.
 */
public enum JPlagMode {
    /**
     * Only run JPlag and create a results.zip
     */
    RUN,
    /**
     * Only start the report viewer
     */
    VIEW,
    /**
     * Run JPlag and open the result in report viewer
     */
    RUN_AND_VIEW
}
