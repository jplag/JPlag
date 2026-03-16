import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * PROJECT L: Stock Market Engine
 * COMPLEXITY: Uses PriorityQueues for order matching,
 * Java Streams for analytics, and internal static classes.
 */
public class StockMarketEngine {

    private static final String[] TICKERS = {"AAPL", "GOOG", "TSLA", "AMZN"};
    private static final Random rand = new Random();

    // Order Books: Buyers want low prices (max heap not needed, just high bids first),
    // Sellers want high prices (min heap for lowest ask).
    // Actually standard matching:
    // Buy Orders: Sort Descending (Highest bid gets priority)
    // Sell Orders: Sort Ascending (Lowest ask gets priority)
    private static PriorityQueue<Order> buySide = new PriorityQueue<>();
    private static PriorityQueue<Order> sellSide = new PriorityQueue<>();

    public static void main(String[] args) {
        System.out.println("--- NYSE Trading System v9.2 ---");

        // Simulate trading day
        for (int i = 0; i < 100; i++) {
            generateRandomOrder();
            matchOrders();
        }

        generateReport();

        //DeadCodeStart
        // Old circuit breaker logic disabled for high-volatility testing
        boolean panicMode = false;
        if (panicMode) {
            haltTrading("MARKET CRASH DETECTED");
            flushAllOrders();
        }
        //DeadCodeEnd
    }

    private static void generateRandomOrder() {
        String ticker = TICKERS[rand.nextInt(TICKERS.length)];
        double price = 100 + (rand.nextDouble() * 50);
        boolean isBuy = rand.nextBoolean();

        Order o = new Order(ticker, price, isBuy);
        if (isBuy) buySide.add(o);
        else sellSide.add(o);
    }

    private static void matchOrders() {
        while (!buySide.isEmpty() && !sellSide.isEmpty()) {
            Order topBuy = buySide.peek();
            Order topSell = sellSide.peek();

            // Spread check: Trade happens if Bid >= Ask
            if (topBuy.price >= topSell.price) {
                // Execute Trade
                buySide.poll();
                sellSide.poll();
                System.out.println(String.format("TRADE EXECUTED: %s @ %.2f", topBuy.ticker, topSell.price));
            } else {
                break; // No more matching prices
            }
        }
    }

    private static void generateReport() {
        System.out.println("\n--- EOD Analytics ---");
        List<Order> openOrders = new ArrayList<>(buySide);
        openOrders.addAll(sellSide);

        // Advanced Stream Usage
        Map<String, Integer> volumeByTicker = openOrders.stream()
                .collect(Collectors.groupingBy(o -> o.ticker, Collectors.counting()));

        volumeByTicker.forEach((k, v) -> System.out.println(k + " Open Orders: " + v));
    }

    //DeadCodeStart
    private static void haltTrading(String reason) {
        System.err.println("HALT: " + reason);
        // This logic is dead because panicMode is hardcoded to false
        buySide.clear();
        sellSide.clear();
    }

    private static void flushAllOrders() {
        // Recursive method that was never implemented correctly
        // flushAllOrders(); // Would cause StackOverflow
    }
    //DeadCodeEnd

    // --- Inner Classes ---

    static class Order {
        String ticker;
        double price;
        boolean isBuy;

        Order(String t, double p, boolean b) {
            this.ticker = t;
            this.price = p;
            this.isBuy = b;
        }

        public double getPrice() {
            return price;
        }
    }
}
