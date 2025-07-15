import java.time.LocalDate;
import java.util.List;

public class Room {

    private final int id;
    private final String type;
    private final double price;

    public Room(int id, String type, double price) {
        this.id = id;
        this.type = type;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalPrice(LocalDate from, LocalDate to) {
        long days = to.toEpochDay() - from.toEpochDay();
        return price * days;
    }

    public boolean isAvailable(LocalDate from, LocalDate to, List<Booking> bookings) {
        return bookings.stream().filter(b -> b.getRoom().getId() == this.id)
                .noneMatch(b -> !(to.isBefore(b.getFrom()) || from.isAfter(b.getTo().minusDays(1))));
    }
}
