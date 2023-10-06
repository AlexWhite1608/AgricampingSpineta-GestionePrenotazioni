package table_stats_controllers;

import data_access.Gateway;
import datasets.DatasetPresenzeController;
import observer.PrenotazioniObservers;
import renderers.TabellaFissaRenderer;
import renderers.TabellaPresenzeRenderer;
import utils.OrderMap;
import utils.TimeManager;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

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

        Map<String, Map<String, Integer>> dataset = DatasetPresenzeController.getTableDataset();
        Set<String> listaAnni = dataset.keySet();
        List<String> listaAnniOrdinati = new ArrayList<>(listaAnni);

        // Ordina la lista degli anni in modo crescente e converte i mesi
        Collections.sort(listaAnniOrdinati);
        Map<String, Map<String, Integer>> datasetConvertito = OrderMap.orderAnniPresenzeMap(dataset);

        // Imposta le colonne (gli anni)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("");
        columnNames.addAll(listaAnniOrdinati);

        // Ottiene i mesi dell'anno da utilizzare come righe del modello
        ArrayList<String> months = TimeManager.getYearMonths(true);

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        for (int i = 0; i < months.size(); i++) {
            String mese = months.get(i);
            Vector<Object> rowData = new Vector<>();
            rowData.add(mese); // Inserisce il mese

            for (int j = 1; j < columnNames.size(); j++) {
                String anno = columnNames.get(j);

                Map<String, Integer> presenzeAnno = datasetConvertito.get(anno);
                if (presenzeAnno != null) {
                    Integer presenzeMese = presenzeAnno.get(mese);
                    rowData.add(presenzeMese != null ? presenzeMese : 0);
                } else {
                    rowData.add(0); // Nessuna presenza per quell'anno e quel mese
                }
            }

            data.add(rowData);
        }

        // Aggiungi l'ultima riga con la stringa "TOTALE"
        Vector<Object> totalRow = new Vector<>();
        totalRow.add("TOTALE");

        for (int j = 1; j < columnNames.size(); j++) {
            int totale = 0;
            for(int k = 0; k < months.size(); k++){
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

        tabellaPresenze.setModel(model);

    }

    // Imposta il renderer per le celle
    public static void createTableRenderer() {
        DefaultTableCellRenderer cellRenderer = new TabellaPresenzeRenderer();
        for(int columnIndex = 0; columnIndex < tabellaPresenze.getColumnCount(); columnIndex++) {
            tabellaPresenze.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }

    }

    // Crea la tabella dei mesi separata
    public static JTable getMesiTable() {

        // Imposta le colonne (gli anni)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Mesi");

        // Ottiene i mesi dell'anno da utilizzare come righe del modello
        ArrayList<String> months = TimeManager.getYearMonths(true);

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        for (int i = 0; i < months.size(); i++) {
            String mese = months.get(i);
            Vector<Object> rowData = new Vector<>();
            rowData.add(mese); // Inserisce il mese

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

        JTable tabellaMesi = new JTable();
        tabellaMesi.setGridColor(Color.BLACK);
        tabellaMesi.getTableHeader().setReorderingAllowed(false);
        tabellaMesi.setModel(model);
        TablePresenzeController.createTabellaMesiRenderer(tabellaMesi, "mesi");

        return tabellaMesi;
    }

    private static void createTabellaMesiRenderer(JTable tabellaMesi, String tipoTabella) {

        DefaultTableCellRenderer cellRenderer = new TabellaFissaRenderer(tipoTabella);
        for(int columnIndex = 0; columnIndex < tabellaMesi.getColumnCount(); columnIndex++) {
            tabellaMesi.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
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
    public void refreshPiazzola() throws SQLException {}

}
