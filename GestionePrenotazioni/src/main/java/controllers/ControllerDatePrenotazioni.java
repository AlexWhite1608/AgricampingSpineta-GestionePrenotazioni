package controllers;

import com.github.lgooddatepicker.components.DatePicker;
import data_access.Gateway;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.format.TextStyle;
import java.util.Locale;

public class ControllerDatePrenotazioni {

    private static LocalDate CURRENT_DATE = LocalDate.now(); // Variabile per memorizzare la data di oggi
    private static final int NUM_DAYS = 180; // Numero di giorni successivi alla data odierna

    // Ritorna le date (dalla corrente fino a CURRENT_DATE + NUM_DAYS) per l'header della tabella Calendario
    public static List<String> getDatesFromCurrentDate() {
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate currentDate = CURRENT_DATE;

        for (int i = 0; i < NUM_DAYS; i++) {
            String formattedDate = currentDate.format(formatter);
            String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ITALIAN).toUpperCase();
            dateList.add(formattedDate + " (" + dayOfWeek.charAt(0) + ")");
            currentDate = currentDate.plusDays(1);
        }

        return dateList;
    }

    // Controlla se è già presente una prenotazione in quelle date per quella piazzola
    public static boolean isAlreadyBooked(String arrivo, String partenza, String piazzola, String idPrenotazione) throws SQLException {
        boolean isAlreadyBooked = false;

//        String overlapPrenotazione = "SELECT COUNT(*) " +
//                "FROM Prenotazioni " +
//                "WHERE Piazzola = ? AND ((Partenza = ?) OR (Arrivo = ?))";
//
//        try {
//            ResultSet rs = new Gateway().execSelectQuery(overlapPrenotazione, piazzola, arrivo, partenza);
//            if (rs.next()) {
//                if (rs.getInt(1) != 0) {
//                    rs.close();
//                    return false;
//                }
//            }
//            rs.close();
//        } catch (SQLException ex) {
//            throw new RuntimeException(ex);
//        }

        String checkPrenotazione;
        if (idPrenotazione == null) {
            checkPrenotazione = "SELECT COUNT(*) " +
                    "FROM Prenotazioni " +
                    "WHERE Piazzola = ? " +
                    "  AND (" +
                    "        (Arrivo <= ? AND Partenza >= ?)" +
                    "        OR (Arrivo <= ? AND Partenza >= ?)" +
                    "        OR (Arrivo >= ? AND Partenza <= ?)" +
                    "        OR (Arrivo <= ? AND Partenza >= ?)" +
                    "      ) AND Partenza <> ?;";
        } else {
            checkPrenotazione = "SELECT COUNT(*) " +
                    "FROM Prenotazioni " +
                    "WHERE Piazzola = ? " +
                    "  AND (" +
                    "        (Arrivo <= ? AND Partenza >= ?)" +
                    "        OR (Arrivo <= ? AND Partenza >= ?)" +
                    "        OR (Arrivo >= ? AND Partenza <= ?)" +
                    "        OR (Arrivo <= ? AND Partenza >= ?)" +
                    "      ) AND Partenza <> ? AND Id <> ?;";
        }

        try {
            ResultSet rs;
            if (idPrenotazione == null) {
                rs = new Gateway().execSelectQuery(checkPrenotazione, piazzola, partenza, arrivo, partenza, partenza, arrivo, partenza, arrivo, arrivo, arrivo);
            } else {
                rs = new Gateway().execSelectQuery(checkPrenotazione, piazzola, partenza, arrivo, partenza, partenza, arrivo, partenza, arrivo, arrivo, arrivo, idPrenotazione);
            }

            if (rs.next()) {
                if(rs.getInt(1) != 0)
                    isAlreadyBooked = true;
            }
            rs.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


        return isAlreadyBooked;
//        boolean isAreadyBooked = false;
//
//        //TODO: ATTENTO A ID_PRENOTAZIONE!
//
//        // Ricavo la lista dei giorni tra arrivo e partenza
//        ArrayList<String> listaGiorni = getDaysFromDates(arrivo, partenza);
//
//        // Controllo, per ciascun giorno in listaGiorni, se si sovrappone con le prenotazioni già presenti
//        String checkPrenotazioneQuery;
//        if(idPrenotazione != null) {
//            checkPrenotazioneQuery = "SELECT COUNT(*) FROM Prenotazioni " +
//                    "WHERE Piazzola = ? AND (" +
//                    "(Arrivo BETWEEN ? AND ?) OR " +
//                    "(Partenza BETWEEN ? AND ?) OR " +
//                    "(Arrivo <= ? AND Partenza >= ?) OR " +
//                    "(Arrivo IN (?, ...) OR Partenza IN (?, ...))" +
//                    ") AND (Id != ?)";
//        } else {
//            checkPrenotazioneQuery = "SELECT COUNT(*) FROM Prenotazioni " +
//                    "WHERE Piazzola = ? AND (" +
//                    "(Arrivo BETWEEN ? AND ?) OR " +
//                    "(Partenza BETWEEN ? AND ?) OR " +
//                    "(Arrivo <= ? AND Partenza >= ?) OR " +
//                    "(Arrivo IN (?, ...) OR Partenza IN (?, ...)))";
//        }
//
//
//        return isAreadyBooked;
    }

    // Controlla che la data di partenza sia successiva a quella di arrivo
    public static void checkOrdineDate(LocalDate arrivo, boolean isNotCorrectOrder, DatePicker datePickerPartenza, JDialog dialogNuovaPrenotazione) {
        if (arrivo != null && isNotCorrectOrder) {
            datePickerPartenza.closePopup();
            MessageController.getErrorMessage(dialogNuovaPrenotazione, "La data di partenza deve essere successiva alla data di arrivo");
            datePickerPartenza.clear();
        }
    }

    // Controlla l'ordine cronologico delle date (fornite come stringhe)
    public static boolean isArrivalBeforeDeparture(String arrivalDateString, String departureDateString) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate arrivalDate = LocalDate.parse(arrivalDateString, dtf);
        LocalDate departureDate = LocalDate.parse(departureDateString, dtf);

        return arrivalDate.isBefore(departureDate);
    }

    // Ricava i giorni tra le date di arrivo e di partenza fornite per tutte le prenotazioni (il primo elemento è sempre il nome della piazzola)
    public static ArrayList<ArrayList<String>> getDaysFromDates(ArrayList<String[]> prenotazioni) {
        ArrayList<ArrayList<String>> listaGiorni = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (String[] info : prenotazioni) {
            String id = info[0];
            String piazzola = info[1];
            LocalDate arrivo = LocalDate.parse(info[2], formatter);
            LocalDate partenza = LocalDate.parse(info[3], formatter);

            LocalDate currentDate = arrivo;
            ArrayList<String> days = new ArrayList<>();

            days.add(id);
            days.add(piazzola);
            while (!currentDate.isAfter(partenza.minusDays(1))) {  // Modificato qui
                days.add(currentDate.format(formatter));
                currentDate = currentDate.plusDays(1);
            }

            listaGiorni.add(days);
        }

        return listaGiorni;
    }

    // Ricava i giorni tra le date di arrivo e di partenza fornite (soltanto una data alla volta)
    public static ArrayList<String> getDaysFromDates(String arrivo, String partenza) {
        ArrayList<String> listaGiorni = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate dataArrivo = LocalDate.parse(arrivo, formatter);
        LocalDate dataPartenza = LocalDate.parse(partenza, formatter);
        LocalDate currentDate = dataArrivo;

        while (!currentDate.isAfter(dataPartenza.minusDays(1))) {
            listaGiorni.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }

        return listaGiorni;
    }

    public static LocalDate getCurrentDate() {
        return CURRENT_DATE;
    }

    public static void setCurrentDate(LocalDate currentDate) {
        CURRENT_DATE = currentDate;
    }
}
