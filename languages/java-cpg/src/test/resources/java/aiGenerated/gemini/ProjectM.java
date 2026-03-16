import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * PROJECT M: Auction House Server
 * PLAGIARISM: Steals the PriorityQueue matching algorithm from StockMarketEngine.
 * - 'Order' becomes 'BidRequest'
 * - 'buySide' becomes 'bidQueue'
 * - 'sellSide' becomes 'listingQueue'
 * * DEAD CODE: Contains logic for "Market Crashes" inside an antique auction app.
 */
public class AuctionHouseServer {

    private static final String[] CATEGORIES = {"Painting", "Vase", "Statue", "Coin"};
    private static final Random rng = new Random();

    // PLAGIARISM: Identical PriorityQueue Logic
    // Bidders want to pay money (sorted Descending to find highest bidder)
    // Sellers want to sell items (sorted Ascending to find lowest reserve price)
    private static PriorityQueue<BidRequest> bidQueue = new PriorityQueue<>();
    private static PriorityQueue<BidRequest> listingQueue = new PriorityQueue<>();

    public static void main(String[] args) {
        System.out.println("--- Sotheby's Digital Auction v2.0 ---");

        // Simulate auction rounds
        for (int i = 0; i < 100; i++) {
            createTraffic();
            resolveAuctions();
        }

        printManifest();

        //DeadCodeStart
        // "Ghost" Code: This is the 'haltTrading' logic from StockMarketEngine.
        // It refers to "SEC Regulations" which makes no sense for an art auction.
        boolean regulatoryFreeze = false;
        if (regulatoryFreeze) {
            System.err.println("SEC HALT: Insider Trading Detected");
            bidQueue.clear();
        }
        //DeadCodeEnd
    }

    private static void createTraffic() {
        String cat = CATEGORIES[rng.nextInt(CATEGORIES.length)];
        double val = 100 + (rng.nextDouble() * 50);
        boolean isBid = rng.nextBoolean();

        BidRequest req = new BidRequest(cat, val, isBid);
        if (isBid) bidQueue.add(req);
        else listingQueue.add(req);
    }

    // PLAGIARISM: This is 'matchOrders' renamed
    private static void resolveAuctions() {
        while (!bidQueue.isEmpty() && !listingQueue.isEmpty()) {
            BidRequest highestBidder = bidQueue.peek();
            BidRequest lowestSeller = listingQueue.peek();

            // Spread check: Deal happens if Bid >= Reserve
            if (highestBidder.amount >= lowestSeller.amount) {
                // Execute Sale
                bidQueue.poll();
                listingQueue.poll();
                System.out.println(String.format("SOLD: %s for $%.2f", highestBidder.itemType, lowestSeller.amount));
            } else {
                break;
            }
        }
    }

    private static void printManifest() {
        System.out.println("\n--- Unsold Inventory ---");
        List<BidRequest> unmatched = new ArrayList<>(bidQueue);
        unmatched.addAll(listingQueue);

        // PLAGIARISM: Identical Stream usage
        Map<String, Long> countByType = unmatched.stream()
                .collect(Collectors.groupingBy(o -> o.itemType, Collectors.counting()));

        countByType.forEach((k, v) -> System.out.println(k + " Pending: " + v));
    }

    //DeadCodeStart

    /**
     * UNUSED ALGORITHM
     * This method contains variable names 'ticker' and 'stockSplit'
     * which were forgotten during the copy-paste process.
     */
    private static void calculateDividends(double stockSplit) {
        String ticker = "AAPL"; // Why is Apple stock in an antique auction?
        double dividend = stockSplit * 0.05;
        // Logic ends here
    }
    //DeadCodeEnd

    // --- Inner Classes ---

    // PLAGIARISM: 'Order' renamed to 'BidRequest'
    static class BidRequest {
        String itemType; // Was 'ticker'
        double amount;   // Was 'price'
        boolean isBid;   // Was 'isBuy'

        BidRequest(String t, double p, boolean b) {
            this.itemType = t;
            this.amount = p;
            this.isBid = b;
        }

        public double getAmount() {
            return amount;
        }
    }
}
