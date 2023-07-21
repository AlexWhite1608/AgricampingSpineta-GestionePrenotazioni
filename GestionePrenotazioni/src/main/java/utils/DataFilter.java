package utils;

import java.time.LocalDate;
import java.util.ArrayList;

// Gestisce i filtri della visualizzazione delle prenotazioni
public class DataFilter {

    private static final String INITIAL_YEAR = "2022";
    private static ArrayList<String> years = new ArrayList<>();
    private static String[] months;

    // Ritorna gli anni dell'attività per il filtraggio delle prenotazioni
    public static ArrayList<String> getYears(){
        years.add("Tutto");     // Mostra tutti gli anni
        years.add(INITIAL_YEAR);
        addCurrentYearIfNotPresent(years);

        return years;
    }

    // Aggiunge l'anno corrente se non è già presente
    private static void addCurrentYearIfNotPresent(ArrayList<String> years) {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        if (!years.contains(currentYear)) {
            years.add(currentYear);
        }
    }

}
