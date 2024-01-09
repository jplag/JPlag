package de.jplag.cli.logger;

import de.jplag.logging.ProgressBar;

public class TongfeiProgressBar implements ProgressBar {
    private final me.tongfei.progressbar.ProgressBar progressBar;

    public TongfeiProgressBar(me.tongfei.progressbar.ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void step(int amount) {
        this.progressBar.stepBy(amount);
    }

    @Override
    public void dispose() {
        this.progressBar.close();
    }
}
