package edu.kit.informatik;

import java.util.ArrayList;
import java.util.List;

public final class Main {

    private int result;
    private int result2;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog());
        animals.add(new Cat());

        for (Animal animal : animals) {
            animal.makeSound();
        }
        
    }
}

public class Animal {
    public Animal() {
        //
    }

    public void makeSound() {
        System.out.println("Some generic animal sound");
    }
}

public class Dog extends Animal {
    public Dog() {
        //
    }

    @Override
    public void makeSound() {
        System.out.println("Bark");
    }
}

public class Cat extends Animal {
    public Cat() {
        //
    }

    @Override
    public void makeSound() {
        System.out.println("Meow");
    }
}
