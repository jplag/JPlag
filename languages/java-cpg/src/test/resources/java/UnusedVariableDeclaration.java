
class UnusedVariableDeclaration {

    private int multiply(int factor1, int factor2) {
        int product = 0;
        for (int i = 0; factor2 > 0; factor2--) {           // <- i is not used
            product += factor1;
        }
        return product;
    }
}