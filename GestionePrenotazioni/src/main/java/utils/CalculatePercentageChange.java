package utils;

public class CalculatePercentageChange {

    public static double calculatePercentageChange(int previousValue, int currentValue) {
        if (previousValue == 0) {
            return 0;
        }

        return ((double) (currentValue - previousValue) / previousValue) * 100;
    }
}
