package edu.kit.informatik;

public final class Main {

    private int result;
    private int result2;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {

        int z = 500;
        int y = 100;

        try {
            z = parsePositiveNumber("250");
            y = y + 100;
        } catch (Exception e) {
            y++;
        }

        result = z;
        result2 = y;
    }

    private int parsePositiveNumber(String input) throws NumberFormatException, ParseException {
        int number = Integer.parseInt(input);
        if (number < 0) {
            throw new ParseException();
        }
        return number;
    }

}
