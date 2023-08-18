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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

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

        //FIXME:
        Map<String, Map<String, Integer>> dataset = DatasetPresenzeController.getTableDataset();
        Set<String> listaAnni = dataset.keySet();
        List<String> listaAnniOrdinati = new ArrayList<>(listaAnni);

        // Ordina la lista degli anni in modo crescente e converte i mesi
        Collections.sort(listaAnniOrdinati);
        Map<String, Map<String, Integer>> datasetConvertito = convertMap(dataset);

        // Imposta le colonne (gli anni)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("");
        columnNames.addAll(listaAnniOrdinati);

        // Ottiene i mesi dell'anno da utilizzare come righe del modello
        ArrayList<String> months = TimeManager.getYearMonths();

        // Imposta i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

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

    //TODO: sposta funzione in Utils??
    private static Map<String, Map<String, Integer>> convertMap(Map<String, Map<String, Integer>> mapToConvert) {
        Map<String, Map<String, Integer>> datasetConMesiItaliani = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : mapToConvert.entrySet()) {
            String anno = entry.getKey();
            Map<String, Integer> mesiPresenze = entry.getValue();
            Map<String, Integer> mesiPresenzeItaliani = new HashMap<>();

            for (Map.Entry<String, Integer> mesePresenzeEntry : mesiPresenze.entrySet()) {
                String meseAnno = mesePresenzeEntry.getKey();
                Integer presenze = mesePresenzeEntry.getValue();

                // Converte il mese dall formato "mm/yyyy" a Month
                String[] parts = meseAnno.split("/");
                int mese = Integer.parseInt(parts[0]);
                Month month = Month.of(mese);

                // Ottieni il nome del mese in italiano con la prima lettera maiuscola
                String nomeMese = month.getDisplayName(TextStyle.FULL, Locale.ITALIAN);
                String meseMaiuscola = nomeMese.substring(0, 1).toUpperCase() + nomeMese.substring(1);

                // Aggiungi alla mappa con i mesi in italiano
                mesiPresenzeItaliani.put(meseMaiuscola, presenze);
            }

            // Aggiungi alla mappa principale con i mesi in italiano
            datasetConMesiItaliani.put(anno, mesiPresenzeItaliani);
        }

        return datasetConMesiItaliani;
    }
}
