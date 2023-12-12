package de.jplag.cli;

import de.jplag.UiHooks;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

/**
 * Provides progress bars for the cli
 */
public class CliUiHooks implements UiHooks {
    private ProgressBar currentProgressBar;

    @Override
    public void startMultiStep(ProgressBarType progressBar, int count) {
        this.currentProgressBar = new ProgressBarBuilder().setTaskName(this.getProgressBarName(progressBar)).setInitialMax(count)
                .setStyle(ProgressBarStyle.UNICODE_BLOCK).build();
    }

    @Override
    public void multiStepStep() {
        if (this.currentProgressBar != null) {
            this.currentProgressBar.step();
            this.currentProgressBar.refresh();
        }
    }

    @Override
    public void multiStepDone() {
        if (this.currentProgressBar != null) {
            this.currentProgressBar.close();
            this.currentProgressBar = null;
        }
    }

    private String getProgressBarName(ProgressBarType progressBarType) {
        return switch (progressBarType) {
            case LOADING -> "Loading Submissions  ";
            case PARSING -> "Parsing Submissions  ";
            case COMPARING -> "Comparing Submissions";
        };
    }
}
