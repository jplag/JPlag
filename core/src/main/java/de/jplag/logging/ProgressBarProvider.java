package de.jplag.logging;

public interface ProgressBarProvider {
    ProgressBar initProgressBar(ProgressBarType type, int totalSteps);
}
