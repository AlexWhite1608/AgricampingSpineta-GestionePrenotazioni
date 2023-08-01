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
    private static final int NUM_DAYS = 30; // Numero di giorni successivi alla data odierna

    // Ritorna le date (dalla corrente fino a CURRENT_DATE + NUM_DAYS) per l'header della tabella Calendario
    public static List<String> getDatesFromCurrentDate() {
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < NUM_DAYS; i++) {
            String formattedDate = CURRENT_DATE.format(formatter);
            String dayOfWeek = CURRENT_DATE.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ITALIAN).toUpperCase();
            dateList.add(formattedDate + " (" + dayOfWeek + ")");
            CURRENT_DATE = CURRENT_DATE.plusDays(1);
        }

        return dateList;
    }

    // Controlla se è già presente una prenotazione in quelle date per quella piazzola
    public static boolean isAlreadyBooked(String arrivo, String partenza, String piazzola) throws SQLException {

        boolean isAlreadyBooked = false;

        // Se un cliente va via ne può arrivare uno nuovo lo stesso giorno nella stessa piazzola
        String overlapPrenotazione = "SELECT COUNT(*) " +
                                     "FROM Prenotazioni " +
                                     "WHERE Piazzola = ? AND Partenza = ?";

        try {
            ResultSet rs = new Gateway().execSelectQuery(overlapPrenotazione, piazzola, arrivo);
            if (rs.next()) {
                rs.close();
                return false;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        // Cerco nel db una prenotazione con le date e la piazzola fornite, se non c'è allora ritorna false, altrimenti true
        String checkPrenotazione = "SELECT COUNT(*) FROM Prenotazioni WHERE Piazzola = ? AND " +
                                   "((Arrivo BETWEEN ? AND ?) OR (Partenza BETWEEN ? AND ?) OR " +
                                   "(? BETWEEN Arrivo AND Partenza) OR (? BETWEEN Arrivo AND Partenza))";
        try {
            ResultSet rs = new Gateway().execSelectQuery(checkPrenotazione, piazzola, arrivo, partenza, arrivo, partenza, arrivo, partenza);
            if (rs.next()) {
                isAlreadyBooked = rs.getInt(1) != 0;
            }
            rs.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return isAlreadyBooked;
    }

    // Controlla che la data di partenza sia successiva a quella di arrivo
    public static void checkOrdineDate(LocalDate arrivo, boolean isNotCorrectOrder, DatePicker datePickerPartenza, JDialog dialogNuovaPrenotazione) {
        if (arrivo != null && isNotCorrectOrder) {
            datePickerPartenza.closePopup();
            MessageController.getErrorMessage(dialogNuovaPrenotazione, "La data di partenza deve essere successiva alla data di arrivo");
            datePickerPartenza.clear();
        }
    }

}
