package de.jplag.cli.logger;

/**
 * A ProgressBar, that used the tongfei progress bar library underneath, to show progress bars on the cli.
 */
public class TongfeiProgressBar extends LogDelayingProgressBar {
    private final me.tongfei.progressbar.ProgressBar progressBar;

    public TongfeiProgressBar(me.tongfei.progressbar.ProgressBar progressBar) {
        super();
        this.progressBar = progressBar;
    }

    @Override
    public void step(int number) {
        this.progressBar.stepBy(number);
    }

    @Override
    public void dispose() {
        this.progressBar.close();
        super.dispose();
    }
}
