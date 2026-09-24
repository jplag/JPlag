package de.jplag.cli.logger;

import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Prints strings to stdout. Provides the option to delay the actual printing.
 */
public class DelayablePrinter {
    private final Queue<String> outputQueue;
    private PrintStream targetStream;

    private boolean isDelayed;

    private static final class InstanceHolder {
        private static final DelayablePrinter instance = new DelayablePrinter();
    }

    /**
     * Threadsafe singleton getter.
     * @return The singleton instance.
     */
    public static DelayablePrinter getInstance() {
        return InstanceHolder.instance;
    }

    private DelayablePrinter() {
        this.outputQueue = new PriorityQueue<>();
        this.targetStream = System.out;
        this.isDelayed = false;
    }

    /**
     * Prints the given string to the terminal appending a line-break.
     * @param output The string to print.
     */
    public synchronized void println(String output) {
        this.outputQueue.offer(output);
        this.printQueue();
    }

    /**
     * Stops printing to the terminal until {@link #resume()} is called.
     */
    public synchronized void delay() {
        this.isDelayed = true;
    }

    /**
     * Resumes printing if {@link #delay()} was called.
     */
    public synchronized void resume() {
        this.isDelayed = false;
        this.printQueue();
    }

    /**
     * Changes the output stream messages are written to.
     * @param printStream is the new output stream.
     */
    public void setOutputStream(PrintStream printStream) {
        this.targetStream = printStream;
    }

    private synchronized void printQueue() {
        if (!this.isDelayed) {
            while (!this.outputQueue.isEmpty()) {
                this.targetStream.println(this.outputQueue.poll());
            }
            this.targetStream.flush();
        }
    }
}
