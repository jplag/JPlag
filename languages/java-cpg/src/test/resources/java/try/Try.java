package de.jplag.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Try {
    public static void main(String[] args) {
        new Try().load("DoesNotExist.txt");
    }

    public void load(String path) {
        Scanner scanner = null;
        try {
            Scanner other; // This is just here to keep the tokens similar.
            scanner = new Scanner(new File(path));
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
