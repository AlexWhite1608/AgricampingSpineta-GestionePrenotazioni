package stats_controllers;

import data_access.Gateway;
import observer.PrenotazioniObservers;
import org.jfree.data.category.DefaultCategoryDataset;
import renderers.TabellaPresenzeRenderer;
import utils.TableConstants;
import utils.TimeManager;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public static void setTableModel() throws SQLException {

        // Imposta le colonne (gli anni)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("");
        columnNames.addAll(TimeManager.getPlotYears());

        // Ottiene i mesi dell'anno da utilizzare come righe del modello
        ArrayList<String> months = TimeManager.getYearMonths();

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        //FIXME:
        DefaultCategoryDataset dataset = DatasetPresenzeController.getDataset();

        for (int i = 0; i < months.size(); i++) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(months.get(i)); // Inserisce il mese

            for (int j = 0; j < columnNames.size(); j++) {

                //TODO: aggiungi il valore effettivo
                rowData.add("prova");
            }

            data.add(rowData);
        }

        // Aggiungi l'ultima riga con la stringa "TOTALE"
        Vector<Object> totalRow = new Vector<>();
        totalRow.add("TOTALE");

        for (int j = 0; j < columnNames.size(); j++) {
            //TODO: aggiungi il valore effettivo per il totale
            totalRow.add("valore_totale");
        }

        data.add(totalRow);

        // Genera il DefaultTableModel con i dati ricavati
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaPresenze.setModel(model);

    }

    // Imposta il renderer per le celle
    public static void createTableRenderer() {
        DefaultTableCellRenderer cellRenderer = new TabellaPresenzeRenderer();
        for(int columnIndex = 0; columnIndex < tabellaPresenze.getColumnCount(); columnIndex++) {
            tabellaPresenze.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }

    }

    @Override
    public void refreshView() throws SQLException {
        //TODO: implementa refresh
    }

    @Override
    public void refreshPiazzola() throws SQLException {}
}
