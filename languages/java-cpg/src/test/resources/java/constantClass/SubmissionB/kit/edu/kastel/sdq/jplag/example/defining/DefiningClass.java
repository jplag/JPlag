package submissionb.edu.kit.kastel.sdq.jplag.example.defining;

public class DefiningClass {

    /**
     * This constant is used in multiple classes.
     */
    public static final String MY_CONSTANT_MULTI_USE = "This is a constant";

    public static void printTheConstant() {

        for (int i = 0; i <= 10; i++) {
            System.out.println("Here comes a constant:");
            System.out.println(MY_CONSTANT_MULTI_USE);
        }

    }

}