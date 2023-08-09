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
import java.time.temporal.ChronoUnit;
import java.util.Vector;

public class TablePartenzeController implements PrenotazioniObservers{

    private static JTable tabellaPartenze;
    private static Gateway gateway;
    private static LocalDate TODAY = LocalDate.now();


    public TablePartenzeController(JTable tabellaPartenze) {

        TablePartenzeController.tabellaPartenze = tabellaPartenze;
        TablePartenzeController.gateway = new Gateway();
    }

    // Imposta il tableModel sulla base della query
    public void setTableModel() throws SQLException {

        // Imposto la query che mi seleziona la vista
        createPartenzeView();
        String query = "SELECT * FROM Partenze WHERE Partenza = ?";

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
            // Calcola la differenza tra le date di arrivo e partenza
            LocalDate arrivo = LocalDate.parse(resultSet.getString("Arrivo"), formatter);
            LocalDate partenza = LocalDate.parse(resultSet.getString("Partenza"), formatter);
            String numNotti = String.valueOf(ChronoUnit.DAYS.between(arrivo, partenza));
            vector.add(numNotti);
            data.add(vector);
        }

        // Genera il DefaultTableModel con i dati ricavati
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaPartenze.setModel(model);
        tabellaPartenze.removeColumn(tabellaPartenze.getColumnModel().getColumn(0));
        resultSet.close();
    }

    // Metodo che crea la vista per la tabella Partenze
    private void createPartenzeView() throws SQLException {
        String dropQuery = "DROP VIEW IF EXISTS Partenze;";
        String viewQueryPartenze = "CREATE VIEW Partenze AS " +
                "SELECT *, " +
                "(julianday(Partenza, 'day') - julianday(Arrivo, 'day')) AS `N° Notti` " +
                "FROM Prenotazioni;";

        // Crea la vista nel database
        gateway.execUpdateQuery(dropQuery);
        gateway.execUpdateQuery(viewQueryPartenze);
    }

    @Override
    public void refreshView() throws SQLException {
        //TODO: implementa refresh!
    }

    @Override
    public void refreshPiazzola() throws SQLException {}
}
