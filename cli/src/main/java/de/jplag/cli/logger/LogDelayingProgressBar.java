package de.jplag.cli.logger;

import de.jplag.logging.ProgressBar;

/**
 * Superclass for progress bars, that delay the log output until the bar is done
 */
public abstract class LogDelayingProgressBar implements ProgressBar {
    protected LogDelayingProgressBar() {
        TerminalPrinter.getInstance().delay();
    }

    @Override
    public void dispose() {
        TerminalPrinter.getInstance().unDelay();
    }
}
