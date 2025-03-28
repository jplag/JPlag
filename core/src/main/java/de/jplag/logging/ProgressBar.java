package de.jplag.logging;

/**
 * Exposed interactions for a running progress bar.
 */
public interface ProgressBar {
    /**
     * Advances the progress bar by a single step.
     */
    default void step() {
        step(1);
    }

    /**
     * Advances the progress bar by amount steps.
     * @param number The number of steps.
     */
    void step(int number);

    /**
     * Closes the progress bar. After this method has been called the behaviour of the other methods is undefined.
     */
    void dispose();
}
