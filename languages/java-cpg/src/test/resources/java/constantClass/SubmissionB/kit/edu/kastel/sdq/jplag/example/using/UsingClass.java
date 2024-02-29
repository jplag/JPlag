package submissionb.edu.kit.kastel.sdq.jplag.example.using;
import submissionb.edu.kit.kastel.sdq.jplag.example.defining.DefiningClass;

public class UsingClass {

    /**
     * This constant is only used in UsingClass.
     */
    public static final String MY_CONSTANT = "This is a constant";

    public static void main(String[] args) {
        System.out.println(MY_CONSTANT);
        System.out.println(DefiningClass.MY_CONSTANT_MULTI_USE);

        while (true) {
            boolean b = true;
        }
    }

}