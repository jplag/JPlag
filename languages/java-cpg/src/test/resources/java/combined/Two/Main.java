// Main.java
//@author Anthropic Claude Sonnet 4.5

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Inheritance Dead Code Analysis ===\n");

        // Only using Dog, never Cat or Bird
        Animal animal = new Dog("Buddy");
        animal.makeSound();
        animal.eat();

        // Using only StandardVehicle
        Vehicle vehicle = new StandardVehicle("Car", 4);
        vehicle.start();
        vehicle.displayInfo();

        // Using only BasicEmployee
        Employee emp = new BasicEmployee("John", 50000);
        emp.work();
        emp.displaySalary();

        // Dead code: ElectricVehicle never instantiated but condition checks for it
        if (vehicle instanceof ElectricVehicle) {
            ((ElectricVehicle) vehicle).charge();
        }

        System.out.println("\n=== Program Complete ===");
    }
}

// Base Animal class
abstract class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public abstract void makeSound();

    public void eat() {
        System.out.println(name + " is eating");
    }

    // Dead method: never called
    public void sleep() {
        System.out.println(name + " is sleeping");
    }

    // Dead method: never overridden or called
    public void move() {
        System.out.println(name + " is moving");
    }
}

// Used class
class Dog extends Animal {
    public Dog(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println(name + " barks: Woof!");
    }

    // Dead method: never called
    public void fetch() {
        System.out.println(name + " is fetching");
    }

    // Dead method: never called
    private void wagTail() {
        System.out.println(name + " is wagging tail");
    }
}

// DEAD CLASS: Never instantiated
class Cat extends Animal {
    private int lives = 9;

    public Cat(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println(name + " meows: Meow!");
    }

    public void purr() {
        System.out.println(name + " is purring");
    }

    private void scratch() {
        System.out.println(name + " is scratching");
    }

    public int getLives() {
        return lives;
    }
}

// DEAD CLASS: Never instantiated
class Bird extends Animal {
    private boolean canFly;

    public Bird(String name, boolean canFly) {
        super(name);
        this.canFly = canFly;
    }

    @Override
    public void makeSound() {
        System.out.println(name + " chirps: Tweet!");
    }

    public void fly() {
        if (canFly) {
            System.out.println(name + " is flying");
        } else {
            System.out.println(name + " cannot fly");
        }
    }

    private void buildNest() {
        System.out.println(name + " is building a nest");
    }
}

// Base Vehicle class
abstract class Vehicle {
    protected String type;
    protected int wheels;

    public Vehicle(String type, int wheels) {
        this.type = type;
        this.wheels = wheels;
    }

    public abstract void start();

    public void displayInfo() {
        System.out.println("Vehicle: " + type + ", Wheels: " + wheels);
    }

    // Dead method: never called
    public void stop() {
        System.out.println(type + " is stopping");
    }

    // Dead method: never called
    protected void honk() {
        System.out.println(type + " is honking");
    }
}

// Used class
class StandardVehicle extends Vehicle {
    public StandardVehicle(String type, int wheels) {
        super(type, wheels);
    }

    @Override
    public void start() {
        System.out.println(type + " engine started");
    }

    // Dead method: never called
    public void refuel() {
        System.out.println(type + " is refueling");
    }
}

// DEAD CLASS: Never instantiated
class ElectricVehicle extends Vehicle {
    private int batteryLevel = 100;

    public ElectricVehicle(String type, int wheels) {
        super(type, wheels);
    }

    @Override
    public void start() {
        System.out.println(type + " electric motor started");
    }

    public void charge() {
        batteryLevel = 100;
        System.out.println(type + " is charging");
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    private void checkBattery() {
        System.out.println("Battery at " + batteryLevel + "%");
    }
}

// DEAD CLASS: Never instantiated
class HybridVehicle extends Vehicle {
    private boolean electricMode = false;

    public HybridVehicle(String type, int wheels) {
        super(type, wheels);
    }

    @Override
    public void start() {
        System.out.println(type + " hybrid system started");
    }

    public void switchMode() {
        electricMode = !electricMode;
        System.out.println("Switched to " + (electricMode ? "electric" : "gas") + " mode");
    }

    private void optimizeEfficiency() {
        System.out.println("Optimizing fuel efficiency");
    }
}

// Base Employee class
abstract class Employee {
    protected String name;
    protected double salary;

    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public abstract void work();

    public void displaySalary() {
        System.out.println(name + "'s salary: $" + salary);
    }

    // Dead method: never called
    public void attendMeeting() {
        System.out.println(name + " is attending a meeting");
    }

    // Dead method: never called
    protected void takeBreak() {
        System.out.println(name + " is taking a break");
    }
}

// Used class
class BasicEmployee extends Employee {
    public BasicEmployee(String name, double salary) {
        super(name, salary);
    }

    @Override
    public void work() {
        System.out.println(name + " is working on tasks");
    }

    // Dead method: never called
    public void submitReport() {
        System.out.println(name + " submitted a report");
    }
}

// DEAD CLASS: Never instantiated
class Manager extends Employee {
    private int teamSize;

    public Manager(String name, double salary, int teamSize) {
        super(name, salary);
        this.teamSize = teamSize;
    }

    @Override
    public void work() {
        System.out.println(name + " is managing team of " + teamSize);
    }

    public void conductReview() {
        System.out.println(name + " is conducting performance reviews");
    }

    private void planStrategy() {
        System.out.println(name + " is planning strategy");
    }
}

// DEAD CLASS: Never instantiated
class Developer extends Employee {
    private String programmingLanguage;

    public Developer(String name, double salary, String language) {
        super(name, salary);
        this.programmingLanguage = language;
    }

    @Override
    public void work() {
        System.out.println(name + " is coding in " + programmingLanguage);
    }

    public void debugCode() {
        System.out.println(name + " is debugging code");
    }

    public void commitCode() {
        System.out.println(name + " is committing code");
    }

    private void reviewPullRequest() {
        System.out.println(name + " is reviewing pull requests");
    }
}

// DEAD CLASS: Never instantiated or extended
abstract class Shape {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    public abstract double getArea();

    public abstract double getPerimeter();

    public void displayColor() {
        System.out.println("Color: " + color);
    }
}

// DEAD CLASS: Never instantiated
class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }

    public double getRadius() {
        return radius;
    }
}

// DEAD CLASS: Never instantiated
class Rectangle extends Shape {
    private double width;
    private double height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double getArea() {
        return width * height;
    }

    @Override
    public double getPerimeter() {
        return 2 * (width + height);
    }

    public boolean isSquare() {
        return width == height;
    }
}

// DEAD CLASS: Never instantiated
class Triangle extends Shape {
    private double side1, side2, side3;

    public Triangle(String color, double s1, double s2, double s3) {
        super(color);
        this.side1 = s1;
        this.side2 = s2;
        this.side3 = s3;
    }

    @Override
    public double getArea() {
        double s = getPerimeter() / 2;
        return Math.sqrt(s * (s - side1) * (s - side2) * (s - side3));
    }

    @Override
    public double getPerimeter() {
        return side1 + side2 + side3;
    }

    public boolean isEquilateral() {
        return side1 == side2 && side2 == side3;
    }
}
