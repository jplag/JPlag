package de.jplag.cli.logger;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 * Prints an idle progress bar, that does not count upwards.
 */
public class IdleBar extends LogDelayingProgressBar {
    private final PrintStream output;

    private final Thread runner;

    private long startTime;
    private final String text;
    private int length;

    private int currentPos;
    private int currentDirection;

    private boolean running = false;

    /**
     * Constructs an IdleBar with the specified label text.
     * @param text Label to display next to the idle bar.
     */
    public IdleBar(String text) {
        super();
        this.output = System.out;
        this.runner = new Thread(this::run);
        this.length = 50;
        this.currentDirection = -1;
        this.currentPos = 0;
        this.text = text;
        try {
            Terminal terminal = TerminalBuilder.terminal();
            this.length = Math.min(terminal.getWidth() / 2, terminal.getWidth() - 50);
            terminal.close();
        } catch (IOException ignore) {
            // ignore exceptions here. If we cannot access the terminal, we guess a width
        }
        if (this.length < 10) {
            this.length = 10;
        }
    }

    /**
     * Starts the idle bar animation in a separate thread.
     */
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
        this.runner.start();
    }

    @Override
    public void dispose() {
        this.running = false;
        try {
            this.runner.join();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        this.output.print('\r');
        this.output.println(this.text + ": complete");
        super.dispose();
    }

    private void run() {
        while (running) {
            this.output.print('\r');
            this.output.print(printLine());
            if (currentPos == 0 || currentPos == length - 1) {
                currentDirection *= -1;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
            currentPos += currentDirection;
        }
    }

    private String printLine() {
        StringBuilder line = new StringBuilder();
        line.append(this.text).append(' ');

        line.append('<');
        line.append(" ".repeat(Math.max(0, currentPos)));
        line.append("<+>");
        line.append(" ".repeat(Math.max(0, length - currentPos - 1)));
        line.append('>');

        long timeRunning = System.currentTimeMillis() - this.startTime;
        line.append(' ');
        String duration = DurationFormatUtils.formatDuration(timeRunning, "H:mm:ss");
        line.append(duration);

        return line.toString();
    }

    @Override
    public void step(int number) {
        // does nothing, because the idle bar has no steps
    }
}
