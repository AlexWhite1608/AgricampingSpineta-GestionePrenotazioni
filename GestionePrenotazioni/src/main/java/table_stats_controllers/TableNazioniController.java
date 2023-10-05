package table_stats_controllers;

import data_access.Gateway;
import datasets.DatasetNazioniController;
import observer.PrenotazioniObservers;
import renderers.TabellaFissaRenderer;
import renderers.TabellaNazioniRenderer;
import utils.OrderMap;
import utils.TableConstants;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class TableNazioniController implements PrenotazioniObservers {

    private static JTable tabellaNazioni;
    private static JTable tabellaNazioniFissa;
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
        Map<String, Map<String, Integer>> datasetConvertito = OrderMap.orderMezziMap(dataset);

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

    // Crea la tabella dei mesi separata
    public static void setNazioniTableModel() throws SQLException {

        // Imposta le colonne
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Nazioni");

        // Ottiene le nazioni da utilizzare come righe del modello
        Map<String, Map<String, Integer>> dataset = DatasetNazioniController.getTableDataset();
        ArrayList<String> listaNazioni = new ArrayList<>();
        for (Map<String, Integer> innerMap : dataset.values()) {
            for (String nazione : innerMap.keySet()){
                if(!Objects.equals(nazione, ""))
                    listaNazioni.add(nazione);
            }
        }

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();
        Set<String> uniqueNazioni = new HashSet<>();

        for (Map<String, Integer> innerMap : dataset.values()) {
            for (String nazione : innerMap.keySet()) {
                if (!Objects.equals(nazione, "") && uniqueNazioni.add(nazione)) {   // Aggiunge la nazione sono se non è doppione nel Set
                    Vector<Object> rowData = new Vector<>();
                    rowData.add(nazione); // Inserisce la nazione

                    data.add(rowData);
                }
            }
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

        tabellaNazioniFissa.setGridColor(Color.BLACK);
        tabellaNazioniFissa.getTableHeader().setReorderingAllowed(false);
        tabellaNazioniFissa.setModel(model);
        TableNazioniController.createTabellaNazioniRenderer(tabellaNazioniFissa, "nazioni");
    }

    private static void createTabellaNazioniRenderer(JTable tabellaMezzi, String tipoTabella) {

        DefaultTableCellRenderer cellRenderer = new TabellaFissaRenderer(tipoTabella);
        for(int columnIndex = 0; columnIndex < tabellaMezzi.getColumnCount(); columnIndex++) {
            tabellaMezzi.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
    }

    // Imposta il renderer per le celle
    public static void createTableRenderer() {
        DefaultTableCellRenderer cellRenderer = new TabellaNazioniRenderer();
        for(int columnIndex = 0; columnIndex < tabellaNazioni.getColumnCount(); columnIndex++) {
            tabellaNazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }

    }

    public static void addNazioniTable(JTable tabellaNazioniFissa) {
        TableNazioniController.tabellaNazioniFissa = tabellaNazioniFissa;
    }

    // Ricarica la tabella
    @Override
    public void refreshView() throws SQLException {

        // Ricostruisce il tableModel con i nuovi valori
        setTableModel();

        setNazioniTableModel();

        createTableRenderer();
    }

    // Non utilizzato!
    @Override
    public void refreshPiazzola() throws SQLException {

    }
}
