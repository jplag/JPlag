package de.jplag.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressBarLogger {
    private static ProgressBarProvider progressBarProvider = new DummyProvider();

    public static ProgressBar createProgressBar(ProgressBarType type, int totalSteps) {
        return progressBarProvider.initProgressBar(type, totalSteps);
    }

    public static void setProgressBarProvider(ProgressBarProvider progressBarProvider) {
        ProgressBarLogger.progressBarProvider = progressBarProvider;
    }

    private static class DummyProvider implements ProgressBarProvider {
        @Override
        public ProgressBar initProgressBar(ProgressBarType type, int totalSteps) {
            return new DummyBar(type, totalSteps);
        }
    }

    private static class DummyBar implements ProgressBar {
        private static final Logger logger = LoggerFactory.getLogger(ProgressBarLogger.class);
        private int currentStep;

        public DummyBar(ProgressBarType type, int totalSteps) {
            this.currentStep = 0;
            logger.info(getProgressBarName(type) + "(" + totalSteps + ")");
        }

        @Override
        public void step() {
            logger.info("Now at step " + this.currentStep++);
        }

        @Override
        public void step(int amount) {
            for (int i = 0; i < amount; i++) {
                step();
            }
        }

        @Override
        public void dispose() {
            logger.info("Progress bar done.");
        }

        private String getProgressBarName(ProgressBarType progressBarType) {
            return switch (progressBarType) {
                case LOADING -> "Loading Submissions  ";
                case PARSING -> "Parsing Submissions  ";
                case COMPARING -> "Comparing Submissions";
            };
        }
    }
}
