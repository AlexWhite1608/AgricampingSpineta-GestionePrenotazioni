package controllers;

import data_access.Gateway;
import observer.PrenotazioniObservers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class TableArriviController implements PrenotazioniObservers {

    private static JTable tabellaArrivi;
    private static Gateway gateway;
    private static LocalDate TODAY = LocalDate.now();

    public TableArriviController(JTable tabellaArrivi) {
        TableArriviController.tabellaArrivi = tabellaArrivi;
        TableArriviController.gateway = new Gateway();
    }

    // Imposta il tableModel sulla base della query
    public void setTableModel() throws SQLException {

        // Imposto la query che mi seleziona la vista
        createArriviView();
        String query = "SELECT * FROM Arrivi WHERE Arrivo = ?";

        // Costruisco il table model
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedTodayDate = TODAY.format(formatter);
        ResultSet resultSet = gateway.execSelectQuery(query, formattedTodayDate);
        ResultSetMetaData metaData = resultSet.getMetaData();

        // Nome delle colonne
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Data
        Vector<Vector<Object>> data = new Vector<>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(resultSet.getObject(columnIndex));
            }
            data.add(vector);
        }

        // Genera il DefaultTableModel con i dati ricavati
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaArrivi.setModel(model);
        tabellaArrivi.removeColumn(tabellaArrivi.getColumnModel().getColumn(0));
        resultSet.close();
    }

    // Metodo che crea la vista per la tabella Arrivi
    private void createArriviView() throws SQLException {
        String dropQuery = "DROP VIEW IF EXISTS Arrivi;";
        String viewQueryArrivi = "CREATE VIEW Arrivi AS " +
                                 "SELECT * FROM Prenotazioni";

        // Crea (o sostituisce) la vista
        gateway.execUpdateQuery(dropQuery);
        gateway.execUpdateQuery(viewQueryArrivi);
    }

    @Override
    public void refreshView() throws SQLException {
        //TODO: implementa refresh!
    }

    @Override
    public void refreshPiazzola() throws SQLException {}
}
