package utils;

import data_access.Gateway;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

// Gestisce i filtri della visualizzazione delle prenotazioni
public class TimeManager {

    private static final String INITIAL_YEAR = "2023";
    private static ArrayList<String> yearsPrenotazioni = new ArrayList<>();

    // Ritorna gli anni dell'attività per il filtraggio delle prenotazioni
    public static ArrayList<String> getPrenotazioniYears() throws SQLException {
        yearsPrenotazioni.add("Tutto");     // Mostra tutti gli anni

        // In base agli anni delle prenotazioni aggiunge eventuali nuovi anni
        String yearsQuery = "SELECT Arrivo, Partenza FROM Prenotazioni";
        ResultSet rs = new Gateway().execSelectQuery(yearsQuery);

        while (rs.next()) {
            String annoArrivo = rs.getString("Arrivo").substring(6, 10);
            String annoPartenza = rs.getString("Partenza").substring(6, 10);

            if(!yearsPrenotazioni.contains(annoArrivo)){
                yearsPrenotazioni.add(annoArrivo);
            } else if (!yearsPrenotazioni.contains(annoPartenza)) {
                yearsPrenotazioni.add(annoPartenza);
            }
        }

        rs.close();

        // Ordina gli anni in ordine decrescente
        yearsPrenotazioni.sort(Collections.reverseOrder());

        return yearsPrenotazioni;
    }

    // Ritorna gli anni dell'attività per il display dei grafici nelle statistiche
    public static ArrayList<String> getPlotYears() throws SQLException {
        ArrayList<String> yearsPlot = new ArrayList<>();

        yearsPlot.add(INITIAL_YEAR);
        addCurrentYearIfNotPresent(yearsPlot);

        // In base agli anni delle prenotazioni aggiunge eventuali nuovi anni
        String yearsQuery = "SELECT Arrivo, Partenza FROM Prenotazioni";
        ResultSet rs = new Gateway().execSelectQuery(yearsQuery);

        while (rs.next()) {
            String annoArrivo = rs.getString("Arrivo").substring(6, 10);
            String annoPartenza = rs.getString("Partenza").substring(6, 10);

            if(!yearsPlot.contains(annoArrivo)){
                yearsPlot.add(annoArrivo);
            } else if (!yearsPlot.contains(annoPartenza)) {
                yearsPlot.add(annoPartenza);
            }
        }

        rs.close();

        // Ordina gli anni in ordine decrescente
        yearsPlot.sort(Collections.reverseOrder());

        return yearsPlot;
    }

    // Aggiunge l'anno corrente se non è già presente
    private static void addCurrentYearIfNotPresent(ArrayList<String> years) {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        if (!years.contains(currentYear)) {
            years.add(currentYear);
        }
    }

    // Ritorna i mesi dell'anno per le statistiche
    public static ArrayList<String> getYearMonths() {
        ArrayList<String> months = new ArrayList<>();

        for (Month month : Month.values()) {
            String meseItaliano = month.getDisplayName(
                    TextStyle.FULL,
                    Locale.ITALIAN
            );

            String meseMaiuscola = meseItaliano.substring(0, 1).toUpperCase() + meseItaliano.substring(1);
            months.add(meseMaiuscola);
        }

        return months;
    }

    // Converte il nome del mese nel corrispettivo numero
    public static String convertiMeseInNumero(String mese) {

        // Crea un dizionario che mappa i nomi dei mesi ai numeri
        Map<String, String> mesi = new HashMap<>();
        mesi.put("Gennaio", "01");
        mesi.put("Febbraio", "02");
        mesi.put("Marzo", "03");
        mesi.put("Aprile", "04");
        mesi.put("Maggio", "05");
        mesi.put("Giugno", "06");
        mesi.put("Luglio", "07");
        mesi.put("Agosto", "08");
        mesi.put("Settembre", "09");
        mesi.put("Ottobre", "10");
        mesi.put("Novembre", "11");
        mesi.put("Dicembre", "12");

        // Controlla se il mese è valido
        if (!mesi.containsKey(mese)) {
            throw new IllegalArgumentException("Mese non valido: " + mese);
        }

        // Ritorna il numero del mese
        return mesi.get(mese);
    }

}
