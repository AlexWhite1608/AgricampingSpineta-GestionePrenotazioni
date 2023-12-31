package table_stats_controllers;

import data_access.Gateway;
import datasets.DatasetMezziController;
import observer.PrenotazioniObservers;
import renderers.TabellaFissaRenderer;
import renderers.TabellaMezziRenderer;
import utils.OrderMap;
import utils.TableConstants;
import utils.TimeManager;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class TableMezziController implements PrenotazioniObservers {

    private static JTable tabellaMezzi;
    private static Gateway gateway;

    public TableMezziController(JTable tabellaMezzi) {
        TableMezziController.tabellaMezzi = tabellaMezzi;
        gateway = new Gateway();

        // Si iscrive alle notifiche del MenuPrenotazioni
        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
    }

    // Imposta il tableModel iniziale della tabella
    public static void setTableModel() throws SQLException {

        Map<String, Map<String, Integer>> dataset = DatasetMezziController.getTableDataset();
        Set<String> listaAnni = dataset.keySet();
        List<String> listaAnniOrdinati = new ArrayList<>(listaAnni);

        // Ordina la lista degli anni in modo crescente
        Collections.sort(listaAnniOrdinati);
        Map<String, Map<String, Integer>> datasetConvertito = OrderMap.orderMezziMap(dataset);

        // Imposta le colonne (gli anni)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("");
        columnNames.addAll(listaAnniOrdinati);

        // Ottiene i mezzi da utilizzare come righe del modello
        ArrayList<String> listaMezzi = TableConstants.listaMezzi;
        listaMezzi.remove("Nessuno");

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        for (int i = 0; i < listaMezzi.size(); i++) {
            String mezzo = listaMezzi.get(i);
            Vector<Object> rowData = new Vector<>();
            rowData.add(mezzo); // Inserisce il mezzo

            for (int j = 1; j < columnNames.size(); j++) {
                String anno = columnNames.get(j);

                Map<String, Integer> mezziAnno = datasetConvertito.get(anno);
                if (mezziAnno != null) {
                    Integer numMezzi = mezziAnno.get(mezzo);
                    rowData.add(numMezzi != null ? numMezzi : 0);
                } else {
                    rowData.add(0); // Nessun mezzo
                }
            }

            data.add(rowData);
        }

        // Aggiungi l'ultima riga con la stringa "TOTALE"
        Vector<Object> totalRow = new Vector<>();
        totalRow.add("TOTALE");

        for (int j = 1; j < columnNames.size(); j++) {
            int totale = 0;
            for(int k = 0; k < listaMezzi.size(); k++){
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

        tabellaMezzi.setModel(model);

    }

    // Imposta il renderer per le celle
    public static void createTableRenderer() {
        DefaultTableCellRenderer cellRenderer = new TabellaMezziRenderer();
        for(int columnIndex = 0; columnIndex < tabellaMezzi.getColumnCount(); columnIndex++) {
            tabellaMezzi.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }

    }

    // Crea la tabella dei mesi separata
    public static JTable getMezziTable() {

        // Imposta le colonne
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Mezzi");

        // Ottiene i mezzi da utilizzare come righe del modello
        ArrayList<String> listaMezzi = TableConstants.listaMezzi;
        listaMezzi.remove("Nessuno");

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        for (int i = 0; i < listaMezzi.size(); i++) {
            String mezzo = listaMezzi.get(i);
            Vector<Object> rowData = new Vector<>();
            rowData.add(mezzo); // Inserisce il mezzo

            data.add(rowData);
        }

        // Aggiungi l'ultima riga con la stringa "TOTALE"
        Vector<Object> totalRow = new Vector<>();
        totalRow.add("TOTALE");

        data.add(totalRow);

        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabellaMezzi = new JTable();
        tabellaMezzi.setGridColor(Color.BLACK);
        tabellaMezzi.getTableHeader().setReorderingAllowed(false);
        tabellaMezzi.setModel(model);
        TableMezziController.createTabellaMezziRenderer(tabellaMezzi, "mezzi");

        return tabellaMezzi;
    }

    private static void createTabellaMezziRenderer(JTable tabellaMezzi, String tipoTabella) {

        DefaultTableCellRenderer cellRenderer = new TabellaFissaRenderer(tipoTabella);
        for(int columnIndex = 0; columnIndex < tabellaMezzi.getColumnCount(); columnIndex++) {
            tabellaMezzi.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
    }

    // Ricarica la tabella
    @Override
    public void refreshView() throws SQLException {

        // Ricostruisce il tableModel con i nuovi valori
        setTableModel();

        createTableRenderer();
    }

    // Non utilizzato!
    @Override
    public void refreshPiazzola() throws SQLException {

    }
}
