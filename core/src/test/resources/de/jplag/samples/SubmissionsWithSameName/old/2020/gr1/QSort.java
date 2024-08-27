import java.util.ArrayList;
import java.util.List;

public class QSort {
    public String[] qsort(String[] array) {
        if (array == null || array.length == 0) {
            return array;
        }
        List<String> sortedList = quickSort(List.of(array));
        return sortedList.toArray(new String[0]);
    }

    private List<String> quickSort(List<String> list) {
        if (list.size() <= 1) {
            return list;
        }

        String pivot = list.get(list.size() / 2);
        List<String> less = new ArrayList<>();
        List<String> equal = new ArrayList<>();
        List<String> greater = new ArrayList<>();

        for (String s : list) {
            int comparison = s.compareTo(pivot);
            if (comparison < 0) {
                less.add(s);
            } else if (comparison > 0) {
                greater.add(s);
            } else {
                equal.add(s);
            }
        }

        List<String> sorted = new ArrayList<>();
        sorted.addAll(quickSort(less));
        sorted.addAll(equal);
        sorted.addAll(quickSort(greater));
        return sorted;
    }
}
