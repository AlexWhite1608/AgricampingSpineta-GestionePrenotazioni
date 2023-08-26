package utils;

import java.util.Comparator;

class MonthComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        int numMese1 = Integer.parseInt(o1.substring(0, 2));
        int numMese2 = Integer.parseInt(o2.substring(0, 2));
        return Integer.compare(numMese1, numMese2);
    }
}
