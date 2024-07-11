package de.jplag.cli.logger;

import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Prints strings to stdout. Provides the option to delay the actual printing.
 */
public class TerminalPrinter {
    private static TerminalPrinter instance;

    /**
     * Threadsafe singleton getter
     * @return The singleton instance
     */
    public static TerminalPrinter getInstance() {
        synchronized (TerminalPrinter.class) {
            if (instance == null) {
                synchronized (TerminalPrinter.class) {
                    instance = new TerminalPrinter();
                }
            }
        }

        return instance;
    }

    private final Queue<String> outputQueue;
    private PrintStream targetStream;

    private boolean isDelayed;

    private TerminalPrinter() {
        this.outputQueue = new PriorityQueue<>();
        this.targetStream = System.out;
        this.isDelayed = false;
    }

    /**
     * Prints the given string to the terminal appending a line-break
     * @param output The string to print
     */
    public void println(String output) {
        synchronized (TerminalPrinter.class) {
            this.outputQueue.offer(output);
            this.printQueue();
        }
    }

    /**
     * Stops printing to the terminal until {@link #unDelay()} is called
     */
    public void delay() {
        synchronized (TerminalPrinter.class) {
            this.isDelayed = true;
        }
    }

    /**
     * Resumes printing if {@link #delay()} was called
     */
    public void unDelay() {
        synchronized (TerminalPrinter.class) {
            this.isDelayed = false;
            this.printQueue();
        }
    }

    /**
     * Changes the output stream messages are written to
     */
    public void setOutputStream(PrintStream printStream) {
        this.targetStream = printStream;
    }

    private void printQueue() {
        synchronized (TerminalPrinter.class) {
            if (!this.isDelayed) {
                while (!this.outputQueue.isEmpty()) {
                    this.targetStream.println(this.outputQueue.poll());
                }
                this.targetStream.flush();
            }
        }
    }
}
