package de.jplag.logging.progressbar;

/**
 * An empty {@link ProgressBar} implementation, used to hide the progress bar depending on the log level.
 */
public class VoidProgressBar implements ProgressBar {
    @Override
    public void step(int number) {
        // does nothing see class description
    }

    @Override
    public void dispose() {
        // does nothing see class description
    }
}
