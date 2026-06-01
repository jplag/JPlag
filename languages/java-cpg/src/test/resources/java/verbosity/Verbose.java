package java;

import java.util.ArrayList;

public class Verbose {
    static int tryCount(ArrayList<Integer> l, int n) {
        if (l.size() == 0) {
            return Integer.MAX_VALUE;
        }

        int count = 0;

        for (int i = 0; i + 1 < l.size(); i++) {
            if (l.get(i) + 1 != l.get(i + 1)) {
                count++;
            }
        }
        if (l.get(0) != 0) {
            count++;
        }

        if (l.get(l.size() - 1) != n - 1) {
            count++;
        }

        return count;
    }
}
