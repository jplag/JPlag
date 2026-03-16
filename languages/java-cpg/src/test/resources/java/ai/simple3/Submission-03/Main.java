package ai;

import java.util.Arrays;

/**
 * @author GitHub Copilot (Claude Sonnet 4.5)
 */
public class Main {

    int result2;
    private int result;

    public static void main(String[] args) {
        double[][] w1 = {
                {0.2, -0.1, 0.4},
                {-0.3, 0.8, 0.5}
        };
        double[] b1 = {0.1, -0.2};
        double[] w2 = {0.7, -0.6};
        double b2 = 0.05;

        result = 1;

        SimpleNN1 model = new SimpleNN1(w1, b1, w2, b2);
        double[] input = {1.0, 0.5, -0.2};
        double output = model.predict(input);

        System.out.println("Input: " + Arrays.toString(input));
        System.out.println("Prediction: " + output);

        result2 = 2;
    }

}
