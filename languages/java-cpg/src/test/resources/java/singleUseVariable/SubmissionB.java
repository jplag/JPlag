package edu.kit.kastel.sdq.jplag.example.singleUse;
public class SubmissionB {

    public void square(int i) {



        System.out.println("Here's the message: '%s'".formatted("%d".formatted(i*i)));
    }

    public static void main(String[] args) {
        new SubmissionB().square(24);
        new SubmissionB().square(7);
    }
}