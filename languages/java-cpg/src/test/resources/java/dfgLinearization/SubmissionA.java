package edu.kit.kastel.sdq.jplag.examples;

public class DfgLinearization {

    public int square() {
        int i = 1;
        int j = 10;
        boolean debug = false;
        System.out.println(++i);
        while (i <= 10) {
            int squared = i*i;
            i++;
            j--;
            System.out.println(squared);
        }
        while (j <= 10) j++;
    }

}