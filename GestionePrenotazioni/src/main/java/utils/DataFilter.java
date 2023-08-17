package utils;

import java.time.LocalDate;
import java.util.ArrayList;

// Gestisce i filtri della visualizzazione delle prenotazioni
public class DataFilter {

    private static final String INITIAL_YEAR = "2023";
    private static ArrayList<String> yearsPrenotazioni = new ArrayList<>();
    private static ArrayList<String> yearsPlot = new ArrayList<>();
    private static String[] months;

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

}
