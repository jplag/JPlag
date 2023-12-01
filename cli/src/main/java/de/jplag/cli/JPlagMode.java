package de.jplag.cli;

/**
 * The mode Jplag runs in. This influences which steps JPlag will execute
 */
public enum JPlagMode {
    /**
     * Only run jplag and create a results.zip
     */
    RUN,
    /**
     * Only start the report viewer
     */
    VIEWER,
    /**
     * Run JPlag and open the result in report viewer
     */
    RUN_AND_VIEW
}
