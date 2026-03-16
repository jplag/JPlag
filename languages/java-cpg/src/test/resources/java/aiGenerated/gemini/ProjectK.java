import java.util.Scanner;

/**
 * PROJECT K: Cinema Ticket Booth
 * PLAGIARISM: Steals logic from FlightBookingSystem.
 * - 'seats' array is structurally identical.
 * - 'bookSeat' becomes 'sellTicket'.
 * - 'cancelSeat' becomes 'refundTicket'.
 * * DEAD CODE: Contains "Ghost" aviation logic that wasn't fully cleaned up.
 */
public class CinemaTicketBooth {

    // PLAGIARISM: Identical array structure (size 5) used for the movie theater row
    private static String[] armchairs = new String[5];

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("--- Grand Cinema Box Office ---");

        boolean showRunning = true;
        while (showRunning) {
            System.out.println("\n1. Sell Ticket");
            System.out.println("2. Refund Ticket");
            System.out.println("3. View Audience");
            System.out.println("4. Close Booth");
            System.out.print("Selection: ");

            String cmd = input.nextLine();

            // PLAGIARISM: Identical Switch-Case logic flow
            if (cmd.equals("1")) {
                sellTicket(input);
            } else if (cmd.equals("2")) {
                refundTicket(input);
            } else if (cmd.equals("3")) {
                viewAudience();
            } else if (cmd.equals("4")) {
                showRunning = false;
            } else {
                System.out.println("Unknown.");
            }

            //DeadCodeStart
            // PLAGIARISM GHOST:
            // This is the 'checkAltimeter' logic from the flight system.
            // It is unreachable (gated by false), but the strings reveal the theft.
            if (false) {
                System.out.println("Altitude: 0ft (Ground Level)");
                // Why would a cinema have oxygen masks?
                // Because this code was stolen from an airplane system.
                System.out.println("DEPLOY OXYGEN MASKS");
            }
            //DeadCodeEnd
        }
    }

    // PLAGIARISM: 'bookSeat' logic copied exactly
    private static void sellTicket(Scanner s) {
        System.out.print("Chair # (0-4): ");
        try {
            int chair = Integer.parseInt(s.nextLine());
            if (chair >= 0 && chair < armchairs.length) {
                if (armchairs[chair] == null) {
                    System.out.print("Viewer Name: ");
                    armchairs[chair] = s.nextLine();
                    System.out.println("Ticket Printed.");
                } else {
                    System.out.println("Chair Taken.");
                }
            } else {
                System.out.println("Invalid Chair.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Input Error.");
        }
    }

    // PLAGIARISM: 'cancelSeat' logic copied exactly
    private static void refundTicket(Scanner s) {
        System.out.print("Chair # (0-4): ");
        try {
            int chair = Integer.parseInt(s.nextLine());
            //DeadCodeStart
            // Useless variable check left over from flight logic
            // "turbulence" has no meaning in a cinema
            boolean turbulence = false;
            if (turbulence) return;
            //DeadCodeEnd

            if (chair >= 0 && chair < armchairs.length) {
                if (armchairs[chair] != null) {
                    System.out.println("Refunded " + armchairs[chair]);
                    armchairs[chair] = null;
                } else {
                    System.out.println("Chair is already free.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error.");
        }
    }

    // PLAGIARISM: 'viewManifest' logic copied exactly
    private static void viewAudience() {
        System.out.println("--- Current Audience ---");
        for (int i = 0; i < armchairs.length; i++) {
            // Logic: (val == null ? empty : val) is identical to source
            System.out.println("Chair " + i + ": " + armchairs[i]);
        }
    }
}
