package java;

import java.util.ArrayList;

public class Compact {
    static int tryCount(ArrayList<Integer> a, int n) {
        if (a.isEmpty())
            return Integer.MAX_VALUE;

        int count = 0;

        for (int i = 0; i + 1 < a.size(); i++)
            if (a.get(i) + 1 != (int) a.get(i + 1))
                count++;

        if (a.get(0) != 0)
            count++;

        if (a.get(a.size() - 1) != n - 1)
            count++;

        return count;
    }
}
