class UnusedVariableDeclaration {
    private int multiply(int factor1, int factor2) {
        int product = 0;
        for (; factor2 > 0; factor2--) {
            product += factor1;

        }

        int productCopy = product;          // Redundant variable copy is another problem
        return productCopy;
    }

    public static void main(String[] args) {
        System.out.println(new UnusedVariableDeclaration().multiply(17, 42));
    }
}