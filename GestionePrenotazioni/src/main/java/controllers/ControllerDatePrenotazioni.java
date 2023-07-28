package controllers;

import com.github.lgooddatepicker.components.DatePicker;
import data_access.Gateway;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ControllerDatePrenotazioni {

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
