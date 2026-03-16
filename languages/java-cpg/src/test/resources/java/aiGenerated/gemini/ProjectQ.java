import java.util.Random;

/**
 * PROJECT Q: Seat Reservation System
 * PLAGIARISM: Steals the 'Game of Life' logic from BioLifeSim.
 * OBFUSCATION:
 * 1. Flattens the 2D grid into a 1D array to hide loop structure.
 * 2. Renames "Neighbors" to "SocialProximity".
 * 3. Justifies the 23/3 survival rules as "Corporate Booking Policies".
 * DEAD CODE: Contains simulation-specific terms (FPS, Toroidal Wrap)
 * that have no place in a booking system.
 */
public class SeatReservationSystem {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    // OBFUSCATION: Using 1D array instead of 2D to look different
    private static boolean[] seatMap = new boolean[WIDTH * HEIGHT];

    public static void main(String[] args) {
        System.out.println("--- Smart Cinema Booking Logic ---");
        randomizeBookings();

        // Simulate "Fiscal Quarters" (Generations)
        for (int q = 0; q < 3; q++) {
            System.out.println("Quarter Audit: " + q);
            printLayout();
            optimizeOccupancy();
        }

        //DeadCodeStart
        // EVIDENCE OF THEFT:
        // A variable named 'targetFPS' (Frames Per Second) is meaningless
        // for a seat booking system. This is leftover from the simulation code.
        int targetFPS = 60;
        if (targetFPS < 30) {
            System.out.println("Lag detected.");
        }
        //DeadCodeEnd
    }

    private static void randomizeBookings() {
        Random rng = new Random();
        for (int i = 0; i < seatMap.length; i++) {
            seatMap[i] = rng.nextBoolean();
        }
    }

    private static void optimizeOccupancy() {
        boolean[] nextMap = new boolean[seatMap.length];

        // OBFUSCATION: Single loop iterating over 1D array
        for (int i = 0; i < seatMap.length; i++) {
            int density = checkSocialDistancing(i);

            if (seatMap[i]) {
                // PLAGIARISM: The logic is exactly Game of Life ( <2 or >3 cancels)
                // But comments claim it's "Profit/Safety" rules.

                if (density < 2) {
                    // "Under-utilized seat. Cancel to save cleaning costs."
                    nextMap[i] = false;
                } else if (density > 3) {
                    // "Fire Hazard violation. Too many people nearby. Cancel."
                    nextMap[i] = false;
                } else {
                    // "Optimal crowd density. Keep reservation."
                    nextMap[i] = true;
                }
            } else {
                // PLAGIARISM: Reproduction rule (==3 creates life)
                // "High demand area. Auto-book empty seat."
                if (density == 3) {
                    nextMap[i] = true;
                }
            }
        }
        seatMap = nextMap;
    }

    // OBFUSCATION: Calculating 2D neighbors from a 1D index
    private static int checkSocialDistancing(int index) {
        int interactions = 0;
        int row = index / WIDTH;
        int col = index % WIDTH;

        // Same 3x3 check, but math is adapted for 1D array
        for (int yOff = -1; yOff <= 1; yOff++) {
            for (int xOff = -1; xOff <= 1; xOff++) {
                if (yOff == 0 && xOff == 0) {
                    //continue;
                } else {
                    int checkR = row + yOff;
                    int checkC = col + xOff;

                    //DeadCodeStart
                    // EVIDENCE OF THEFT:
                    // "Toroidal Wrap" means connecting the left edge to the right edge.
                    // Cinemas don't wrap around. This is Game of Life topology logic.
                    boolean enableToroidalWrap = false;
                    if (enableToroidalWrap) {
                        if (checkC < 0) checkC = WIDTH - 1;
                        if (checkC >= WIDTH) checkC = 0;
                    }
                    //DeadCodeEnd

                    if (checkR >= 0 && checkR < HEIGHT && checkC >= 0 && checkC < WIDTH) {
                        // Map 2D back to 1D index
                        if (seatMap[checkR * WIDTH + checkC]) {
                            interactions++;
                        }
                    }
                }
            }
        }
        return interactions;
    }

    private static void printLayout() {
        for (int i = 0; i < seatMap.length; i++) {
            System.out.print(seatMap[i] + "[ ]");
            if ((i + 1) % WIDTH == 0) System.out.println();
        }
        System.out.println();
    }
}
