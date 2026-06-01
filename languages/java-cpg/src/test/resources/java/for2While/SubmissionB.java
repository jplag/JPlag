
class For2While {

    private int multiply(int factor1, int factor2) {
        int product = 0;
        {
            int i = 0, j = 3;
            while (factor2 > 0) {
                {
                    product += factor1;
                    String a = "Also not used.";
                }
                factor2--;
            }
        }
        double d = 4.0;                                    // <- d is not used
        int productCopy = product;
        return productCopy;
    }

    public static void main(String[] args) {
        System.out.println(new UnusedVariableDeclaration().multiply(17, 42));
    }
}