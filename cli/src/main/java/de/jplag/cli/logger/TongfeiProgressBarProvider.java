package de.jplag.cli.logger;

import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarProvider;
import de.jplag.logging.ProgressBarType;

import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

/**
 * A ProgressBar provider, that used the tongfei progress bar library underneath, to show progress bars on the cli.
 */
public class TongfeiProgressBarProvider implements ProgressBarProvider {
    @Override
    public ProgressBar initProgressBar(ProgressBarType type, int totalSteps) {
        me.tongfei.progressbar.ProgressBar progressBar = new ProgressBarBuilder().setTaskName(type.getDefaultText()).setInitialMax(totalSteps)
                .setStyle(ProgressBarStyle.ASCII).build();
        return new TongfeiProgressBar(progressBar);
    }
}
