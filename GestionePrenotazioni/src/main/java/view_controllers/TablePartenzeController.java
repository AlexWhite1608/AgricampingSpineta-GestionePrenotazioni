package view_controllers;

import data_access.Gateway;
import observer.PrenotazioniObservers;
import renderers.ArriviPartenzeRenderer;
import utils.TableConstants;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        // Si iscrive alle notifiche del MenuPrenotazioni
        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
    }

    // Imposta il tableModel sulla base della query
    public void setTableModel() throws SQLException {

        // Imposto la query che mi seleziona la vista
        String query = "SELECT * FROM Prenotazioni WHERE Partenza = ?";

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
        columnNames.add("N° Notti");

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

    // Renderizza le celle
    public static void createCellRenderer() {
        DefaultTableCellRenderer cellRenderer = new ArriviPartenzeRenderer();
        for(int columnIndex = 0; columnIndex < tabellaPartenze.getColumnCount(); columnIndex++) {
            tabellaPartenze.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
    }

    // Renderizza l'header
    public static void createHeaderRenderer() {
        DefaultTableCellRenderer headerRenderer = getHeader();
        tabellaPartenze.getTableHeader().setDefaultRenderer(headerRenderer);
    }

    // Inizializza l'header renderer
    private static DefaultTableCellRenderer getHeader() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(TableConstants.HEADER_FONT_CALENDARIO);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setOpaque(false);

                return c;
            }
        };
    }

    @Override
    public void refreshView() throws SQLException {
        setTableModel();
        createCellRenderer();
    }

    @Override
    public void refreshPiazzola() throws SQLException {}

    public static LocalDate getTODAY() {
        return TODAY;
    }

    public static void setTODAY(LocalDate TODAY) {
        TablePartenzeController.TODAY = TODAY;
    }
}
