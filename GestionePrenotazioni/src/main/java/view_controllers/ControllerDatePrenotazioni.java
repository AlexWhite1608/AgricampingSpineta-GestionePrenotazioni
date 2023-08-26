package view_controllers;

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
import java.util.Objects;

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

        String checkPrenotazione;
        if (idPrenotazione == null) {
            checkPrenotazione = "SELECT Arrivo, Partenza " +
                    "FROM Prenotazioni " +
                    "WHERE Piazzola = ? ";
        } else {
            checkPrenotazione = "SELECT Arrivo, Partenza " +
                    "FROM Prenotazioni " +
                    "WHERE Piazzola = ? " +
                    "AND ID <> ?";
        }

        try {
            ResultSet rs;
            if (idPrenotazione == null) {
                rs = new Gateway().execSelectQuery(checkPrenotazione, piazzola);
            } else {
                rs = new Gateway().execSelectQuery(checkPrenotazione, piazzola, idPrenotazione);
            }

            while (rs.next()) {

                LocalDate newArrivoData = LocalDate.parse(arrivo, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate newPartenzaData = LocalDate.parse(partenza, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate oldArrivoData = LocalDate.parse(rs.getString("Arrivo"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate oldPartenzaData = LocalDate.parse(rs.getString("Partenza"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String oldArrivoStringa = rs.getString("Arrivo");
                String oldPartenzaStringa = rs.getString("Partenza");

                boolean condition1 = (oldArrivoData.isBefore(newArrivoData) && oldPartenzaData.isAfter(newArrivoData));
                boolean condition2 = (oldArrivoData.isBefore(newPartenzaData) && oldPartenzaData.isAfter(newPartenzaData));
                boolean condition3 = (oldArrivoData.isBefore(newArrivoData) && oldPartenzaData.isAfter(newPartenzaData));
                boolean condition4 = (oldArrivoData.isAfter(newArrivoData) && oldPartenzaData.isBefore(newPartenzaData));
                boolean condition5 = (Objects.equals(oldArrivoStringa, arrivo) || Objects.equals(oldPartenzaStringa, partenza));

                if (condition1 || condition2 || condition3 || condition4 || condition5) {
                    isAlreadyBooked = true;

                    if (condition1) {
                        System.out.println("Nuova data di arrivo interna ad una prenotazione");
                    }
                    if (condition2) {
                        System.out.println("Nuova data di partenza interna ad una prenotazione");
                    }
                    if (condition3) {
                        System.out.println("Nuova data arrivo e nuova data partenza interne ad una prenotazione");
                    }
                    if (condition4) {
                        System.out.println("Nuova data arrivo e nuota data partenza esterne ad una prenotazione");
                    }
                    if (condition5) {
                        System.out.println("Una tra arrivo/partenza coincide con una prenotazione");
                    }
                    break;
                } else {

                    // Verifica il caso limite in cui arrivo/partenza possono coincidere
                    if(Objects.equals(oldArrivoStringa, partenza) || Objects.equals(oldPartenzaStringa, arrivo)) {

                        // Verifico che i giorni compresi tra le nuove date non vadano ad interferire con tutte le altre date!
                        ArrayList<String> listOfNewDays = getDatesBetween(arrivo, partenza);
                        ArrayList<String> listOfOldDays = getDatesBetween(oldArrivoStringa, oldPartenzaStringa);

                        for(String newDay : listOfNewDays) {
                            if(listOfOldDays.contains(newDay)){
                                isAlreadyBooked = true;
                                break;
                            }
                        }
                    }
                }
            }
            rs.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return isAlreadyBooked;
    }

    // Ottiene tutti i giorni compresi tra le date indicate
    private static ArrayList<String> getDatesBetween(String startDate, String endDate) {
        ArrayList<String> dates = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Incrementa la data di partenza di un giorno
        LocalDate current = start.plusDays(1);

        while (!current.isAfter(end.minusDays(1))) {
            dates.add(current.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            current = current.plusDays(1);
        }

        return dates;
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
