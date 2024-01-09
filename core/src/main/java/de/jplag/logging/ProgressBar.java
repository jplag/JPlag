package de.jplag.logging;

public interface ProgressBar {
    default void step() {
        step(1);
    }

    void step(int amount);

    void dispose();
}
