package de.jplag.cli.logger;

import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Queue;

public class TerminalPrinter {
    private static TerminalPrinter instance;

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
    private final PrintStream targetStream;

    private boolean isDelayed;

    private TerminalPrinter() {
        this.outputQueue = new PriorityQueue<>();
        this.targetStream = System.out;
        this.isDelayed = false;
    }

    public void println(String output) {
        synchronized (TerminalPrinter.class) {
            this.outputQueue.offer(output);
            this.printQueue();
        }
    }

    public void delay() {
        synchronized (TerminalPrinter.class) {
            this.isDelayed = true;
        }
    }

    public void unDelay() {
        synchronized (TerminalPrinter.class) {
            this.isDelayed = false;
            this.printQueue();
        }
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
