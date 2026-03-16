interface Playable {
    void play(String filename);

    default void pause() {
        System.out.println("Paused");
    }
}

interface Trainable {
    static void displayTrainingTips() {
        System.out.println("Use positive reinforcement");
    }

    void train(String command);
}

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Inheritance Dead Code Analysis ===\n");

        Animal polymorphicDog = new Dog();
        polymorphicDog.breathe();

        System.out.println("\n=== Program Complete ===");
    }
}

abstract class LivingBeing {
    protected String species;

    public abstract void makeSound();

    public final void live() {
        System.out.println("Living being is alive");
    }
}

class Animal extends LivingBeing implements Playable {
    private int age;

    public Animal() {
        this.species = "Unknown";
        this.age = 0;
    }

    public Animal(String species, int age) {
        this.species = species;
        this.age = age;
    }

    @Override
    public void play(String filename) {
        System.out.println("Animal playing: " + filename);
    }

    public void breathe() {
        System.out.println(species + " is breathing");
    }

    @Override
    public void makeSound() {
        System.out.println("Generic animal sound");
    }

    protected void sleep() {
        System.out.println("Animal is sleeping");
    }
}

class LandAnimal extends Animal {
    private int numberOfLegs;

    public LandAnimal() {
        super();
        this.numberOfLegs = 4;
    }

    public LandAnimal(String species, int age, int legs) {
        super(species, age);
        this.numberOfLegs = legs;
    }

    public void swim() {
        this.breathe();
        System.out.println("Land animal swimming with " + numberOfLegs + " legs");
    }

    @Override
    public void makeSound() {
        super.makeSound();
        System.out.println("Land-based sound");
    }
}

class Dog extends LandAnimal implements Trainable {
    private String breed;

    public Dog() {
        super("Canine", 0, 4);
        this.breed = "Generic";
    }

    public Dog(String breed, int age) {
        super("Canine", age, 4);
        this.breed = breed;
    }

    @Override
    public void makeSound() {
        System.out.println("Woof!");
    }

    public void bark() {
        makeSound();
    }

    @Override
    public void train(String command) {
        System.out.println("Dog learning: " + command);
    }

    @Override
    public void play(String filename) {
        super.play(filename);
        System.out.println("Dog enjoys playing");
    }
}

class Husky extends Dog {
    private boolean canPullSled;

    public Husky() {
        super("Husky", 0);
        this.canPullSled = true;
    }

    public Husky(int age) {
        super("Husky", age);
        this.canPullSled = true;
    }

    @Override
    public void makeSound() {
        System.out.println("Awoo! (Husky howl)");
    }

    @Override
    public void train(String command) {
        super.train(command);
        System.out.println("Husky mastered: " + command);
    }

    public void howl() {
        makeSound();
    }

    public void pullSled() {
        if (canPullSled) {
            System.out.println("Husky is pulling a sled");
        }
    }
}
