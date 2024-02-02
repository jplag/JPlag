package de.jplag.logging;

/**
 * Exposed interactions for a running progress bar.
 */
public interface ProgressBar {
    /**
     * Advances the progress bar by a single step
     */
    default void step() {
        step(1);
    }

    /**
     * Advances the progress bar by amount steps
     * @param amount The amount of steps
     */
    void step(int amount);

    /**
     * Closes the progress bar. After this method has been called the behaviour of the other methods is undefined.
     */
    void dispose();
}
