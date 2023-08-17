package utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

// Gestisce i filtri della visualizzazione delle prenotazioni
public class TimeManager {

    private static final String INITIAL_YEAR = "2023";
    private static ArrayList<String> yearsPrenotazioni = new ArrayList<>();
    private static ArrayList<String> yearsPlot = new ArrayList<>();

    // Ritorna gli anni dell'attività per il filtraggio delle prenotazioni
    public static ArrayList<String> getPrenotazioniYears(){
        yearsPrenotazioni.add("Tutto");     // Mostra tutti gli anni
        yearsPrenotazioni.add(INITIAL_YEAR);
        addCurrentYearIfNotPresent(yearsPrenotazioni);

        return yearsPrenotazioni;
    }

    // Ritorna gli anni dell'attività per il display dei grafici nelle statistiche
    public static ArrayList<String> getPlotYears(){

        yearsPlot.add(INITIAL_YEAR);
        addCurrentYearIfNotPresent(yearsPlot);

        return yearsPlot;
    }

    // Aggiunge l'anno corrente se non è già presente
    private static void addCurrentYearIfNotPresent(ArrayList<String> years) {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        if (!years.contains(currentYear)) {
            years.add(currentYear);
        }
    }

    public static ArrayList<String> getYearMonths() {
        ArrayList<String> months = new ArrayList<>();

        for (Month month : Month.values()) {
            String meseItaliano = month.getDisplayName(
                    TextStyle.FULL,
                    Locale.ITALIAN
            );
            months.add(meseItaliano);
        }

        return months;
    }

}
