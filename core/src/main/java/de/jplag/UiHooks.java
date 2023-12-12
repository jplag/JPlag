package de.jplag;

/**
 * Notifies the ui of state changes in JPlag
 */
public interface UiHooks {
    /**
     * A null ui hook, that does nothing
     */
    UiHooks NullUiHooks = new UiHooks() {
        @Override
        public void startMultiStep(ProgressBarType progressBar, int count) {
        }

        @Override
        public void multiStepStep() {
        }

        @Override
        public void multiStepDone() {
        }
    };

    /**
     * Starts a new multi-step process
     * @param progressBar The type of process
     * @param count The number of steps
     */
    void startMultiStep(ProgressBarType progressBar, int count);

    /**
     * Advances the process by one step
     */
    void multiStepStep();

    /**
     * Ends the multi-step process
     */
    void multiStepDone();

    /**
     * The available processes
     */
    enum ProgressBarType {
        LOADING,
        PARSING,
        COMPARING
    }
}
