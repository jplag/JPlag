package edu.kit.kastel.sdq.jplag.examples;

/**
 * Taken from  Moritz Brödel
 */
public class DfgLinearization {
    public int square() {
        int i = 1;
        //boolean debug removed by singleUseVariable transformation
        System.out.println(++i);
        while (i <= 10) {
            int square = i * i;
            System.out.println(square);
            i++;
        }
    }

}