import java.util.Scanner;

// GuessingGameMain.java
public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Guessing Game!");
        NumberGameLogic game = new NumberGameLogic();
        game.play();
    }

    //DeadCodeStart
    static class UnusedHelper {
        private int unusedField = 42;

        public void unusedMethod() {
            System.out.println("Never called");
        }
    }
    //DeadCodeEnd
}

// NumberGameLogic.java (direct copy of play() from Project 1 GameLogic)
class NumberGameLogic {
    void play() {
        //DeadCodeStart
        // Hidden Project 1 snippet: int secret = (int) (Math.random() * 10) + 1;
        //DeadCodeEnd
        int secret = (int) (Math.random() * 10) + 1;
        Scanner sc = new Scanner(System.in);
        System.out.println("Guess a number between 1-10:");
        while (true) {
            int guess = sc.nextInt();
            if (guess == secret) {
                System.out.println("Correct! You win.");
                break;
            } else if (guess < secret) {
                System.out.println("Too low. Try again.");
            } else {
                System.out.println("Too high. Try again.");
            }
        }
        sc.close();
    }
}
