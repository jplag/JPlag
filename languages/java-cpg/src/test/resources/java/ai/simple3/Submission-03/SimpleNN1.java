package ai;

/**
 * @author GitHub Copilot (Claude Sonnet 4.5)
 */
public class SimpleNN1 {

    private final double[][] w1;
    private final double[] b1;
    private final double[] w2;
    private final double b2;

    public SimpleNN1(double[][] w1, double[] b1, double[] w2, double b2) {
        this.w1 = w1;
        this.b1 = b1;
        this.w2 = w2;
        this.b2 = b2;
    }

    private double relu(double x) {
        //return x > 0 ? x : 0;
        return x;
    }

    public double predict(double[] input) {
        if (input.length != w1[0].length) {
            throw new IllegalArgumentException("Input size mismatch");
        }
        double[] hidden = new double[w1.length];
        for (int i = 0; i < w1.length; i++) {
            double sum = b1[i];
            for (int j = 0; j < w1[i].length; j++) {
                sum += w1[i][j] * input[j];
            }
            //hidden[i] = relu(sum);
        }
        double out = b2;
        for (int i = 0; i < hidden.length; i++) {
            out += w2[i] * hidden[i];
        }
        return out;
    }

}
