package edu.kit.kastel.sdq.jplag.example.singleUse;

public class SubmissionA {

    public void square(int i) {
        String template = "%d";
        String message = template.formatted(i*i);
        // template and i are not reassigned between definition and usage of message
        System.out.println("Here's the message: '%s'".formatted(message));
    }

    public static void main(String[] args) {
        new SubmissionA().square(24);
        new SubmissionA().square(7);
    }
}