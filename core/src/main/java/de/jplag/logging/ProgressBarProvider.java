package de.jplag.logging;

/**
 * Provides the capability to create new progress bars, to allow JPlag to access the ui.
 */
public interface ProgressBarProvider {
    /**
     * Creates a new progress bar.
     * @param type The type of progress bar. Should mostly determine the name.
     * @param totalSteps The total number of steps the progress bar should have.
     * @return The newly created bar.
     */
    ProgressBar initProgressBar(ProgressBarType type, int totalSteps);
}
