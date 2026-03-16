// DeadFactoryPattern.java
//@author Anthropic Claude Sonnet 4.5

import java.util.ArrayList;
import java.util.List;

// DEAD STRATEGY PATTERN: Never used
interface PaymentStrategy {
    void pay(double amount);
}

// DEAD OBSERVER PATTERN: Never used
interface EventListener {
    void onEvent(String event);
}

// DEAD DECORATOR PATTERN: Never used
interface Coffee {
    double cost();

    String description();
}

// DEAD ADAPTER PATTERN: Never used
interface MediaPlayer {
    void play(String filename);
}

interface AdvancedMediaPlayer {
    void playVlc(String filename);

    void playMp4(String filename);
}

// DEAD FACTORY CLASS: Never used
class CreatureFactory {
    public static Creature createCreature(String type) {
        switch (type.toLowerCase()) {
            case "dog":
                return new FactoryDog();
            case "cat":
                return new FactoryCat();
            case "bird":
                return new FactoryBird();
            default:
                return null;
        }
    }
}

// DEAD BASE CLASS for factory pattern
abstract class Creature {
    protected String species;

    public abstract void makeNoise();

    public void describe() {
        System.out.println("This is a " + species);
    }
}

// DEAD CLASSES: Factory implementations never used
class FactoryDog extends Creature {
    public FactoryDog() {
        this.species = "Dog";
    }

    @Override
    public void makeNoise() {
        System.out.println("Woof!");
    }

    public void fetch() {
        System.out.println("Fetching stick");
    }
}

class FactoryCat extends Creature {
    public FactoryCat() {
        this.species = "Cat";
    }

    @Override
    public void makeNoise() {
        System.out.println("Meow!");
    }

    public void purr() {
        System.out.println("Purring");
    }
}

class FactoryBird extends Creature {
    public FactoryBird() {
        this.species = "Bird";
    }

    @Override
    public void makeNoise() {
        System.out.println("Tweet!");
    }

    public void flyAway() {
        System.out.println("Flying high");
    }
}

// DEAD SINGLETON PATTERN: Never accessed
class DatabaseConnection {
    private static DatabaseConnection instance;
    private String connectionString;

    private DatabaseConnection() {
        connectionString = "localhost:5432";
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void connect() {
        System.out.println("Connected to " + connectionString);
    }

    public void disconnect() {
        System.out.println("Disconnected");
    }

    public void executeQuery(String query) {
        System.out.println("Executing: " + query);
    }
}

// DEAD BUILDER PATTERN: Never used
class PersonBuilder {
    private String name;
    private int age;
    private String address;
    private String phone;

    public PersonBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder setAge(int age) {
        this.age = age;
        return this;
    }

    public PersonBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public PersonBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public PersonData build() {
        return new PersonData(name, age, address, phone);
    }
}

// DEAD CLASS: Result of builder
class PersonData {
    private final String name;
    private final int age;
    private final String address;
    private final String phone;

    public PersonData(String name, int age, String address, String phone) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age +
                ", address='" + address + "', phone='" + phone + "'}";
    }
}

class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " with credit card ending in " +
                cardNumber.substring(cardNumber.length() - 4));
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " via PayPal account " + email);
    }
}

class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    private double total;

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    public void addItem(double price) {
        total += price;
    }

    public void checkout() {
        if (paymentStrategy != null) {
            paymentStrategy.pay(total);
            total = 0;
        }
    }
}

class EventPublisher {
    private List<EventListener> listeners = new ArrayList<>();

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(String event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}

class EmailListener implements EventListener {
    private String email;

    public EmailListener(String email) {
        this.email = email;
    }

    @Override
    public void onEvent(String event) {
        System.out.println("Email to " + email + ": " + event);
    }
}

class SMSListener implements EventListener {
    private String phone;

    public SMSListener(String phone) {
        this.phone = phone;
    }

    @Override
    public void onEvent(String event) {
        System.out.println("SMS to " + phone + ": " + event);
    }
}

class SimpleCoffee implements Coffee {
    @Override
    public double cost() {
        return 2.0;
    }

    @Override
    public String description() {
        return "Simple coffee";
    }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double cost() {
        return coffee.cost() + 0.5;
    }

    @Override
    public String description() {
        return coffee.description() + ", milk";
    }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double cost() {
        return coffee.cost() + 0.2;
    }

    @Override
    public String description() {
        return coffee.description() + ", sugar";
    }
}

class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String filename) {
        System.out.println("Playing vlc file: " + filename);
    }

    @Override
    public void playMp4(String filename) {
        // Do nothing
    }
}

class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String filename) {
        // Do nothing
    }

    @Override
    public void playMp4(String filename) {
        System.out.println("Playing mp4 file: " + filename);
    }
}

class MediaAdapter implements MediaPlayer {
    AdvancedMediaPlayer advancedPlayer;

    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer = new VlcPlayer();
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedPlayer = new Mp4Player();
        }
    }

    @Override
    public void play(String filename) {
        if (filename.endsWith(".vlc")) {
            advancedPlayer.playVlc(filename);
        } else if (filename.endsWith(".mp4")) {
            advancedPlayer.playMp4(filename);
        }
    }
}

class AudioPlayer implements MediaPlayer {
    MediaAdapter mediaAdapter;

    @Override
    public void play(String filename) {
        if (filename.endsWith(".mp3")) {
            System.out.println("Playing mp3 file: " + filename);
        } else if (filename.endsWith(".vlc") || filename.endsWith(".mp4")) {
            mediaAdapter = new MediaAdapter(filename.substring(filename.lastIndexOf('.') + 1));
            mediaAdapter.play(filename);
        } else {
            System.out.println("Invalid format");
        }
    }
}
