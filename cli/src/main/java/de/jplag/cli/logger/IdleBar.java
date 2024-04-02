package de.jplag.cli.logger;

import java.io.IOException;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import de.jplag.logging.ProgressBar;

/**
 * Prints an idle progress bar, that does not count upwards.
 */
public class IdleBar implements ProgressBar {
    private final Thread runner;

    private long startTime;
    private final String text;
    private int length;

    String emptyLine;

    private int currentPos;
    private int currentDirection;

    private boolean running = false;

    public IdleBar(String text) {
        this.runner = new Thread(this::run);
        this.length = 50;
        this.currentDirection = -1;
        this.currentPos = 0;
        this.text = text;
        try {
            Terminal terminal = TerminalBuilder.terminal();
            this.length = terminal.getWidth() / 2;
            terminal.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (this.length < 10) {
            this.length = 10;
        }

        StringBuilder empty = new StringBuilder();
        empty.append('\r');
        empty.append(" ".repeat(Math.max(0, length + 4 + text.length() + 10)));
        empty.append('\r');
        this.emptyLine = empty.toString();
    }

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
        }
        System.out.println();
    }

    private void run() {
        while (running) {
            System.out.print('\r');
            System.out.print(printLine());
            if (currentPos == 0 || currentPos == length - 1) {
                currentDirection *= -1;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
                // ignore wakeup
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
    }

    public static void main(String[] args) throws InterruptedException {
        IdleBar bar = new IdleBar("Printing progress");
        bar.start();
        Thread.sleep(10000);
        bar.dispose();
    }
}
