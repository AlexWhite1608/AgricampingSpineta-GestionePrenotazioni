package utils;

public class CalculatePercentage {

    // Calcola la variazione percentuale per le tabelle nelle statistiche rispetto all'anno precedente
    public static double calculatePercentageChange(int previousValue, int currentValue) {
        if (previousValue == 0) {
            if (currentValue > 0) {
                return currentValue * 100; // Incremento positivo infinito
            } else {
                return 0;
            }
        }

        return ((double) (currentValue - previousValue) / previousValue) * 100;
    }

}
