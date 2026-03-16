import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PROJECT H: Smart Home Hub
 * Advanced Example: Uses abstract classes and polymorphism.
 * Simulates a central hub managing various IOT devices.
 */
public class SmartHomeHub {

    private static final List<SmartDevice> devices = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("--- Smart Home Hub v3.0 ---");

        devices.add(new SmartBulb("Living Room Light"));
        devices.add(new SmartThermostat("Hallway AC"));

        simulateDayCycle();

        //DeadCodeStart
        // Legacy voice activation module that was disabled due to privacy concerns
        boolean voiceEnabled = false;
        if (voiceEnabled) {
            listenForCommands();
        }
        //DeadCodeEnd
    }

    private static void simulateDayCycle() {
        Random rand = new Random();
        for (int hour = 0; hour < 24; hour++) {
            System.out.println("Hour: " + hour + ":00");

            // Polymerphism in action
            for (SmartDevice device : devices) {
                if (rand.nextBoolean()) {
                    device.toggle();
                }
                if (device.isOn) {
                    device.operate();
                }
            }

            //DeadCodeStart
            // Unreachable code intended for a demo mode that forces all devices on
            if (hour == 99) {
                for (SmartDevice d : devices) d.isOn = true;
            }
            //DeadCodeEnd
        }
    }

    //DeadCodeStart
    private static void listenForCommands() {
        System.out.println("Listening for 'Hey Java'...");
        // Logic incomplete
    }
    //DeadCodeEnd

    // --- Inner Classes for Device Hierarchy ---

    abstract static class SmartDevice {
        String id;
        boolean isOn = false;

        SmartDevice(String id) {
            this.id = id;
        }

        void toggle() {
            isOn = !isOn;
            System.out.println(id + " is now " + (isOn + "OFF"));
        }

        abstract void operate();
    }

    static class SmartBulb extends SmartDevice {
        SmartBulb(String id) {
            super(id);
        }

        @Override
        void operate() {
            System.out.println("  -> " + id + " is illuminating at 800 lumens.");
        }
    }

    static class SmartThermostat extends SmartDevice {
        SmartThermostat(String id) {
            super(id);
        }

        @Override
        void operate() {
            System.out.println("  -> " + id + " is regulating temp to 22C.");
        }
    }
}
