import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Hotel {
    private final int id;
    private final String city;
    private final List<Room> rooms = new ArrayList<>();

    public Hotel(int id, String city) {
        this.id = id;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void removeRoom(int roomId) {
        Iterator<Room> iterator = rooms.iterator();
        while (iterator.hasNext()) {
            Room room = iterator.next();
            if (room.getId() == roomId) {
                iterator.remove();
                break;
            }
        }
    }

    public Room getRoom(int roomId) {
        for (Room room : rooms) {
            if (room.getId() == roomId) {
                return room;
            }
        }
        return null;
    }
}
