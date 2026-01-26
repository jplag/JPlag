package de.jplag.cli.logger;

import java.util.Set;

import org.slf4j.event.Level;

import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarProvider;
import de.jplag.logging.ProgressBarType;

import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

/**
 * A ProgressBar provider, that used the tongfei progress bar library underneath, to show progress bars on the cli.
 */
public class CliProgressBarProvider implements ProgressBarProvider {
    private static final Set<Level> allowedLogLevels = Set.of(Level.INFO);

    @Override
    public ProgressBar initProgressBar(ProgressBarType type, int totalSteps) {
        if (!allowedLogLevels.contains(CollectedLogger.getLogLevel())) {
            return new VoidProgressBar();
        }
        if (type.isIdleBar()) {
            IdleBar idleBar = new IdleBar(type.getDefaultText());
            idleBar.start();
            return idleBar;
        }
        me.tongfei.progressbar.ProgressBar progressBar = new ProgressBarBuilder().setTaskName(type.getDefaultText()).setInitialMax(totalSteps)
                .setStyle(ProgressBarStyle.ASCII).build();
        return new TongfeiProgressBar(progressBar);
    }
}
