package de.jplag.cli.options;

/**
 * The mode JPlag runs in. This influences which steps JPlag will execute.
 */
public enum JPlagMode {
    /**
     * Only run JPlag and create a results.jplag.
     */
    RUN,
    /**
     * Only start the report viewer.
     */
    VIEW,
    /**
     * Run JPlag and open the result in report viewer.
     */
    RUN_AND_VIEW,
    /**
     * Choose the mode automatically from the given input files.
     */
    AUTO,
}
