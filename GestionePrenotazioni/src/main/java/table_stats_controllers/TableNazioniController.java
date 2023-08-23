package table_stats_controllers;

import data_access.Gateway;
import datasets.DatasetMezziController;
import datasets.DatasetNazioniController;
import observer.PrenotazioniObservers;
import renderers.TabellaMezziRenderer;
import renderers.TabellaNazioniRenderer;
import utils.TableConstants;
import utils.TimeManager;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.*;

public class TableNazioniController implements PrenotazioniObservers {

    private static JTable tabellaNazioni;
    private static Gateway gateway;

    public TableNazioniController(JTable tabellaNazioni) {
        TableNazioniController.tabellaNazioni = tabellaNazioni;
        gateway = new Gateway();

        // Si iscrive alle notifiche del MenuPrenotazioni
        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
    }

    // Imposta il tableModel iniziale della tabella
    public static void setTableModel() throws SQLException {

        Map<String, Map<String, Integer>> dataset = DatasetNazioniController.getTableDataset();
        Set<String> listaAnni = dataset.keySet();
        List<String> listaAnniOrdinati = new ArrayList<>(listaAnni);

        // Ordina la lista degli anni in modo crescente
        Collections.sort(listaAnniOrdinati);
        Map<String, Map<String, Integer>> datasetConvertito = TimeManager.orderMezziMap(dataset);

        // Imposta le colonne (gli anni)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("");
        columnNames.addAll(listaAnniOrdinati);

        // Ottiene le nazioni da utilizzare come righe del modello
        ArrayList<String> listaNazioni = new ArrayList<>();
        for (Map<String, Integer> innerMap : dataset.values()) {
            for (String nazione : innerMap.keySet()){
                if(!Objects.equals(nazione, ""))
                    listaNazioni.add(nazione);
            }
        }

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        for (int i = 0; i < listaNazioni.size(); i++) {
            String nazione = listaNazioni.get(i);
            Vector<Object> rowData = new Vector<>();
            rowData.add(nazione); // Inserisce la nazione

            for (int j = 1; j < columnNames.size(); j++) {
                String anno = columnNames.get(j);

                Map<String, Integer> nazioniAnno = datasetConvertito.get(anno);
                if (nazioniAnno != null) {
                    Integer numPresenzeNazioni = nazioniAnno.get(nazione);
                    rowData.add(numPresenzeNazioni != null ? numPresenzeNazioni : 0);
                } else {
                    rowData.add(0); // Nessuna nazione
                }
            }

            data.add(rowData);
        }

        // Aggiungi l'ultima riga con la stringa "TOTALE"
        Vector<Object> totalRow = new Vector<>();
        totalRow.add("TOTALE");

        for (int j = 1; j < columnNames.size(); j++) {
            int totale = 0;
            for(int k = 0; k < listaNazioni.size(); k++){
                totale += (int) data.get(k).get(j);
            }
            totalRow.add(totale);
        }

        data.add(totalRow);

        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaNazioni.setModel(model);

    }

    // Imposta il renderer per le celle
    public static void createTableRenderer() {
        DefaultTableCellRenderer cellRenderer = new TabellaNazioniRenderer();
        for(int columnIndex = 0; columnIndex < tabellaNazioni.getColumnCount(); columnIndex++) {
            tabellaNazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }

    }

    @Override
    public void refreshView() throws SQLException {

    }

    @Override
    public void refreshPiazzola() throws SQLException {

    }
}
