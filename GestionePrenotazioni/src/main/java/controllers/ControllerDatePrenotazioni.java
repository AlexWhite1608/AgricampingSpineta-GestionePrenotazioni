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

        JTable tabellaCalendario = TableCalendarioController.getTabellaCalendario();
        boolean isAlreadyBooked = false;

        // Calcola indice della riga della piazzola corrispondente
        int rowIndex = -1;
        for (int row = 0; row < tabellaCalendario.getRowCount(); row++) {
            if (piazzola.equals(tabellaCalendario.getValueAt(row, 0))) {
                rowIndex = row;
                break;
            }
        }

        if (rowIndex != -1) {

            // Ricava gli indici delle colonne in base alle date di arrivo e partenza
            int colonnaDataArrivo = -1;
            int colonnaDataPartenza = -1;
            for (int col = 1; col < tabellaCalendario.getColumnCount(); col++) {
                if (tabellaCalendario.getColumnName(col).contains(arrivo)) {
                    colonnaDataArrivo = col;
                }
                if (tabellaCalendario.getColumnName(col).contains(partenza)) {
                    colonnaDataPartenza = col;
                }
                if (colonnaDataArrivo != -1 && colonnaDataPartenza != -1) {
                    break;
                }
            }

            // Controlla se le date sono già prenotate
            for (int col = colonnaDataArrivo; col <= colonnaDataPartenza - 1; col++) {
                if (!tabellaCalendario.getValueAt(rowIndex, col).equals(0)) {
                    isAlreadyBooked = true;
                    break;
                }
            }
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

    // Controlla l'ordine cronologico delle date (fornite come stringhe)
    public static boolean isArrivalBeforeDeparture(String arrivalDateString, String departureDateString) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate arrivalDate = LocalDate.parse(arrivalDateString, dtf);
        LocalDate departureDate = LocalDate.parse(departureDateString, dtf);

        return arrivalDate.isBefore(departureDate);
    }

    public static LocalDate getCurrentDate() {
        return CURRENT_DATE;
    }

    public static void setCurrentDate(LocalDate currentDate) {
        CURRENT_DATE = currentDate;
    }
}
