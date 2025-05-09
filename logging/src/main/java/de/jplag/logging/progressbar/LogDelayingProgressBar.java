package de.jplag.logging.progressbar;

import de.jplag.logging.DelayablePrinter;

/**
 * Superclass for progress bars, that delay the log output until the bar is done
 */
public abstract class LogDelayingProgressBar implements ProgressBar {
    protected LogDelayingProgressBar() {
        DelayablePrinter.getInstance().delay();
    }

    @Override
    public void dispose() {
        DelayablePrinter.getInstance().resume();
    }
}
