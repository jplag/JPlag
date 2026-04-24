package de.jplag.logging;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides static access to the creation of progress bars.
 */
public class ProgressBarLogger {
    private static ProgressBarProvider progressBarProvider = new DummyProvider();

    private ProgressBarLogger() {
        // Hides default constructor
    }

    /**
     * Creates a new {@link ProgressBar}.
     * @param type The type of the progress bar.
     * @param totalSteps The total number of steps.
     * @return The newly created progress bar.
     */
    public static ProgressBar createProgressBar(ProgressBarType type, int totalSteps) {
        return progressBarProvider.initProgressBar(type, totalSteps);
    }

    /**
     * Sets the {@link ProgressBarProvider}. Should be used by the ui before calling JPlag, if progress bars should be
     * shown.
     * @param progressBarProvider The provider
     */
    public static void setProgressBarProvider(ProgressBarProvider progressBarProvider) {
        ProgressBarLogger.progressBarProvider = progressBarProvider;
    }

    /**
     * Iterates over the given collection while showing and updating a progress bar of the given type. The progress bar is
     * updated, everytime the given action is done.
     * @param type The type of progress bar
     * @param data The data to iterate over
     * @param action The action to call for each item
     * @param <T> The type of data
     */
    public static <T> void iterate(ProgressBarType type, Collection<T> data, Consumer<T> action) {
        Iterator<T> iterator = data.iterator();
        ProgressBar progressBar = ProgressBarLogger.createProgressBar(type, data.size());

        while (iterator.hasNext()) {
            action.accept(iterator.next());
            progressBar.step();
        }

        progressBar.dispose();
    }

    private static class DummyProvider implements ProgressBarProvider {
        @Override
        public ProgressBar initProgressBar(ProgressBarType type, int totalSteps) {
            return new DummyBar(type, totalSteps);
        }
    }

    private static class DummyBar implements ProgressBar {
        private static final Logger logger = LoggerFactory.getLogger(DummyBar.class);
        private final ProgressBarType type;
        private int currentStep;

        public DummyBar(ProgressBarType type, int totalSteps) {
            this.type = type;
            currentStep = 0;
            if (type.isIdleBar()) {
                logger.info("{} - started", type.getDefaultText());
            } else {
                logger.info("{} ({})", type.getDefaultText(), totalSteps);
            }
        }

        @Override
        public void step() {
            logger.info("{} - step {}", type.getDefaultText(), this.currentStep++);
        }

        @Override
        public void step(int number) {
            for (int i = 0; i < number; i++) {
                step();
            }
        }

        @Override
        public void dispose() {
            logger.info("Progress bar done.");
        }
    }
}
