package de.jplag.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TryWithResource {
    public static void main(String[] args) {
        new TryWithResource().load("DoesNotExist.txt");
    }

    public void load(String path) {
        Scanner other = null; // This is just here to keep the tokens similar.
        try (Scanner scanner = other = new Scanner(new File(path))) { // same for = other =
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } finally {
            if (other != null) { // This as well...
                other.close(); // This as well...
            }
        }
    }
}
