package de.jplag.pdf.utils;

import java.text.DecimalFormat;

public class MathUtils {
    public static int roundUpTwoSignificantDigits(int number) {
        int orderOfMagnitude = (int) Math.floor(Math.log10(number)) - 1;

        if (orderOfMagnitude < 0) {
            return 10;
        }

        int firstTwoDigits = number / (int) Math.pow(10, orderOfMagnitude);

        return (firstTwoDigits + 1) * (int) Math.pow(10, orderOfMagnitude);
    }

    public static String convertToPercent(double value) {
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(value);
    }

    public static int getNumberOfDigits(int number) {
        return (number == 0) ? 1 : (int) Math.log10(number) + 1;
    }
}
