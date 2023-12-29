class UnusedVariableDeclaration {
    private int multiply(int factor1, int factor2) {
        int product = 0;
        for (int i = 0, j = 3; factor2 > 0; factor2--) {           // <- i and j are not used
            product += factor1;
            String a = "Also not used.";                    // <- a is not used
        }
        double d = 4.0;                                    // <- d is not used
        int productCopy = product;
        return productCopy;
    }

    public static void main(String[] args) {
        System.out.println(new UnusedVariableDeclaration().multiply(17, 42));
    }
}