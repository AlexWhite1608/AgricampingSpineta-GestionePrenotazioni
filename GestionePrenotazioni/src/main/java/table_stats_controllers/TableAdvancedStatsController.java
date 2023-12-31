package table_stats_controllers;

import data_access.Gateway;
import datasets.DatasetMezziController;
import datasets.DatasetNazioniController;
import datasets.DatasetPresenzeController;
import renderers.TabellaAdvStatsNazioniMezziRenderer;
import renderers.TabellaAdvStatsNazioniRenderer;
import renderers.TabellaPresenzeRenderer;
import utils.TimeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TableAdvancedStatsController {

    private JTable tableStatsNazioni;
    private JTable tableStatsMezziNazioni;

    // Il vecchio giorno con più presenze
    private static final String MAX_PRESENZE_GIORNO = "14/08/2023";
    private static final int NUM_PRESENZE_MAX_GIORNO = 88;

    public TableAdvancedStatsController() {
    }

    // Ritorna il mese con più presenze
    public static String getMesePiuPresenze() throws SQLException {
        String result = null;

        // Ottiene i dati sulle presenze
        Map<String, Map<String, Integer>> map = DatasetPresenzeController.getPresenzeForMese();

        int presenzeMassime = Integer.MIN_VALUE;
        for (Map.Entry<String, Map<String, Integer>> entryAnni : map.entrySet()) {
            for (Map.Entry<String, Integer> entryPresenze : entryAnni.getValue().entrySet()) {
                String anno = entryAnni.getKey();
                String mese = entryPresenze.getKey().substring(0, 2);
                int presenze = entryPresenze.getValue();

                if (presenze > presenzeMassime) {
                    presenzeMassime = presenze;
                    result = mese + "/" + anno + " (" + presenze + ")";
                }
            }
        }

        return result;
    }

    // Ritorna il mezzo più utilizzato
    public static String getMezzoPiuUsato() throws SQLException {
        String result = null;

        // Ottiene i dati sui mezzi
        Map<String, Map<String, Integer>> map = DatasetMezziController.getCountMezzi();

        int numMezziMassimo = Integer.MIN_VALUE;
        for(Map.Entry<String, Map<String, Integer>> entryAnni : map.entrySet()){
            for(Map.Entry<String, Integer> entryMezzi : entryAnni.getValue().entrySet()){
                String mezzo = entryMezzi.getKey();
                int numMezzi = entryMezzi.getValue();

                if (numMezzi > numMezziMassimo && !Objects.equals(mezzo, "")) {
                    numMezziMassimo = numMezzi;
                    result = mezzo;
                }
            }

        }

        return result;
    }

    // Ritorna la durata media del soggiorno
    public static String getDurataMediaSoggiorno() throws SQLException {
        double result = 0;

        ArrayList<Integer> list = DatasetPresenzeController.getDurataPrenotazioni();

        for (Integer element : list) {
            result += element;
        }

        double media = result / list.size();
        // Arrotonda la media a due cifre decimali
        media = Math.round(media * 100.0) / 100.0;

        return String.valueOf(media);
    }

    // Ritorna il numero di presenze di oggi
    public static String getPresenzeOggi() throws SQLException {
        int totalePersone = 0;
        String query = "SELECT Nome, Arrivo, Partenza, Persone " +
                       "FROM Prenotazioni ";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedTodayDate = TimeManager.TODAY.format(formatter);

        ResultSet resultSet = new Gateway().execSelectQuery(query);

        while(resultSet.next()){
            String nome = resultSet.getString("Nome");

            // Non si considerano i nomi delle prenotazioni che sono i mesi usati per le stats
            if(!TimeManager.getYearMonths(false).contains(nome)){
                String arrivo = resultSet.getString("Arrivo");
                String partenza = resultSet.getString("Partenza");
                int persone = resultSet.getInt("Persone");

                LocalDate dataArrivo = LocalDate.parse(arrivo, formatter);
                LocalDate dataPartenza = LocalDate.parse(partenza, formatter);
                LocalDate oggi = LocalDate.parse(formattedTodayDate, formatter);

                boolean condition1 = dataArrivo.isBefore(oggi) && dataPartenza.isAfter(oggi);
                boolean condition2 = dataArrivo.equals(oggi) && dataPartenza.isAfter(oggi);
                if(condition1 || condition2){
                    totalePersone += persone;
                }
            }

        }

        resultSet.close();

        return String.valueOf(totalePersone);
    }

    // Ritorna il giorno con le presenze maggiori (rispetto al 14/08/2023)
    public static String getMaxPresenzeGiorno() throws SQLException {
        int presenzeOggi = Integer.parseInt(TableAdvancedStatsController.getPresenzeOggi());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String oggi = TimeManager.TODAY.format(formatter);

        if(presenzeOggi > NUM_PRESENZE_MAX_GIORNO)
            return oggi + " (" + presenzeOggi + ")";
        else
            return MAX_PRESENZE_GIORNO + " (" + NUM_PRESENZE_MAX_GIORNO + ")";
    }

    // Crea il table model per la tabella delle nazioni
    public void setTableModelNazioni(String annoScelto, String meseScelto) throws SQLException {

        // Ottieni il dataset delle presenze per anno e mese
        Map<String, Map<String, Map<String, Integer>>> dataset = DatasetPresenzeController.getPresenzeForMeseAndNazione();

        // Imposta le colonne del table model
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Nazione");
        columnNames.add("Presenze " + meseScelto + "/" + annoScelto);

        // Crea i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        // Itera attraverso le nazioni nel dataset
        for (Map.Entry<String, Map<String, Map<String, Integer>>> nazioneEntry : dataset.entrySet()) {
            String anno = nazioneEntry.getKey();

            if(Objects.equals(anno, annoScelto)) {
                Map<String, Map<String, Integer>> datiNazione = nazioneEntry.getValue();

                // Ottieni il numero di presenze per la nazione specificata
                for(Map.Entry<String, Map<String, Integer>> entry : datiNazione.entrySet()){
                    String nazione = entry.getKey();

                    for(Map.Entry<String, Integer> datiPresenze : entry.getValue().entrySet()){
                        Vector<Object> rowData = new Vector<>();
                        int presenze = datiPresenze.getValue();

                        if(Objects.equals(datiPresenze.getKey().substring(0, 2), TimeManager.convertiMeseInNumero(meseScelto))) {

                            // Aggiungi la riga al modello
                            rowData.add(nazione);
                            rowData.add(presenze);

                            data.add(rowData);
                        }

                    }
                }
            }
        }

        // Crea il modello della tabella
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Imposta il modello sulla tabella
        tableStatsNazioni.setModel(model);
    }

    // Crea il table model per la tabella delle nazioni e dei mezzi
    public void setTableModelNazioniMezzi(String annoScelto, String meseScelto, String nazioneScelta) throws SQLException {

        // Ottiene il dataset
        Map<String, Map<String, Map<String, Map<String, Integer>>>> dataset = DatasetMezziController.getNumeroVeicoliNazione();

        // Imposta le colonne del table model
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Mezzo");
        columnNames.add("Numero " + meseScelto + "/" + annoScelto);

        // Crea i dati del modello
        Vector<Vector<Object>> data = new Vector<>();

        // Itera attraverso le nazioni nel dataset
        for (Map.Entry<String, Map<String, Map<String, Map<String, Integer>>>> nazioneEntry : dataset.entrySet()) {
            String anno = nazioneEntry.getKey();

            if(Objects.equals(anno, annoScelto)) {
                Map<String, Map<String, Map<String, Integer>>> datiNazione = nazioneEntry.getValue();

                // Ottieni il numero di presenze per la nazione specificata
                for(Map.Entry<String, Map<String, Map<String, Integer>>> entry : datiNazione.entrySet()){
                    String nazione = entry.getKey();

                    if(Objects.equals(nazione, nazioneScelta)) {
                        for(Map.Entry<String, Map<String, Integer>> datiMezzi : entry.getValue().entrySet()){
                            for(Map.Entry<String, Integer> datiNumeroMezzi : datiMezzi.getValue().entrySet()){
                                Vector<Object> rowData = new Vector<>();

                                String mezzo = datiNumeroMezzi.getKey();
                                int numeroMezzi = datiNumeroMezzi.getValue();
                                String meseAttuale = datiMezzi.getKey().substring(0, 2);

                                if(Objects.equals(meseAttuale, TimeManager.convertiMeseInNumero(meseScelto))) {

                                    // Aggiungi la riga al modello
                                    rowData.add(mezzo);
                                    rowData.add(numeroMezzi);

                                    data.add(rowData);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Crea il modello della tabella
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Imposta il modello sulla tabella
        tableStatsMezziNazioni.setModel(model);
    }

    public void createTableNazioniRenderer() {
        DefaultTableCellRenderer cellRenderer = new TabellaAdvStatsNazioniRenderer();
        for(int columnIndex = 0; columnIndex < this.tableStatsNazioni.getColumnCount(); columnIndex++) {
            this.tableStatsNazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
    }

    public void createTableNazioniMezziRenderer() {
        DefaultTableCellRenderer cellRenderer = new TabellaAdvStatsNazioniMezziRenderer();
        for(int columnIndex = 0; columnIndex < this.tableStatsMezziNazioni.getColumnCount(); columnIndex++) {
            this.tableStatsMezziNazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
    }

    public void setTableStatsNazioni(JTable tableStatsNazioni) {
        this.tableStatsNazioni = tableStatsNazioni;
    }

    public void setTableStatsMezziNazioni(JTable tableStatsMezziNazioni) {
        this.tableStatsMezziNazioni = tableStatsMezziNazioni;
    }
}
