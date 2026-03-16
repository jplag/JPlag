import java.util.Scanner;

/**
 * PROJECT J: Flight Booking System
 * A system to manage seat assignments on a small aircraft.
 */
public class FlightBookingSystem {

    // 5 seats, null = empty, String = passenger name
    private static String[] seats = new String[5];

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("--- SkyHigh Airlines Reservation ---");

        boolean flying = true;
        while (flying) {
            System.out.println("\n1. Book Seat");
            System.out.println("2. Cancel Reservation");
            System.out.println("3. View Manifest");
            System.out.println("4. Land (Exit)");
            System.out.print("Option: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    bookSeat(sc);
                    break;
                case "2":
                    cancelSeat(sc);
                    break;
                case "3":
                    viewManifest();
                    break;
                case "4":
                    flying = false;
                    break;
                default:
                    System.out.println("Invalid.");
            }

            //DeadCodeStart
            // Safety check that was disabled after beta testing
            if (false) {
                checkAltimeter();
            }
            //DeadCodeEnd
        }
    }

    private static void bookSeat(Scanner sc) {
        System.out.print("Enter Seat # (0-4): ");
        try {
            int seat = Integer.parseInt(sc.nextLine());
            if (seat >= 0 && seat < seats.length) {
                if (seats[seat] == null) {
                    System.out.print("Passenger Name: ");
                    seats[seat] = sc.nextLine();
                    System.out.println("Boarding Pass Issued.");
                } else {
                    System.out.println("Seat Occupied.");
                }
            } else {
                System.out.println("Invalid Seat.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error reading input.");
        }
    }

    private static void cancelSeat(Scanner sc) {
        System.out.print("Enter Seat # (0-4): ");
        try {
            int seat = Integer.parseInt(sc.nextLine());
            if (seat >= 0 && seat < seats.length) {
                if (seats[seat] != null) {
                    System.out.println("Removed " + seats[seat]);
                    seats[seat] = null;
                } else {
                    System.out.println("Seat is already empty.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error.");
        }
    }

    private static void viewManifest() {
        System.out.println("--- Passenger Manifest ---");
        for (int i = 0; i < seats.length; i++) {
            System.out.println("Seat " + i + ": " + seats[i]);
        }
    }

    //DeadCodeStart
    private static void checkAltimeter() {
        System.out.println("Altitude: 30,000ft");
        // Dead logic intended to trigger oxygen masks if pressure drops
        double pressure = 100.0;
        if (pressure < 50) {
            System.out.println("DEPLOY MASKS");
        }
    }
    //DeadCodeEnd
}
