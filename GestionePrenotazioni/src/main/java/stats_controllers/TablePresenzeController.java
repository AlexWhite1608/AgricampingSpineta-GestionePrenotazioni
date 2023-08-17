package stats_controllers;

import data_access.Gateway;
import observer.PrenotazioniObservers;
import utils.TimeManager;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class TablePresenzeController implements PrenotazioniObservers {

    private static JTable tabellaPresenze;
    private static Gateway gateway;

    public TablePresenzeController(JTable tabellaPresenze) {
        TablePresenzeController.tabellaPresenze = tabellaPresenze;
        gateway = new Gateway();

        // Si iscrive alle notifiche del MenuPrenotazioni
        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
    }

    // Imposta il tableModel iniziale della tabella
    public static void setTableModel() {

        // Imposta le colonne (gli anni)
        Vector<String> columnNames = new Vector<>(TimeManager.getPlotYears());

        // Ottiene i mesi dell'anno da utilizzare come righe del modello
        ArrayList<String> months = TimeManager.getYearMonths();

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        for (int i = 0; i < months.size(); i++) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(months.get(i)); // Inserisce il mese

            for (int j = 0; j < columnNames.size(); j++) {

                rowData.add("prova");
            }

            data.add(rowData);
        }

        // Genera il DefaultTableModel con i dati ricavati
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaPresenze.setModel(model);

    }

    @Override
    public void refreshView() throws SQLException {
        //TODO: implementa refresh
    }

    @Override
    public void refreshPiazzola() throws SQLException {}
}
