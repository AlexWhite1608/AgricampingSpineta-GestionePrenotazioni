package table_stats_controllers;

import datasets.DatasetMezziController;
import datasets.DatasetPresenzeController;
import utils.TimeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

public class TableAdvancedStatsController {

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

    // Crea il table model per la tabella delle nazioni
    public static void setTableModelNazioni(JTable table, String annoScelto, String meseScelto) throws SQLException {

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
        table.setModel(model);
    }


}
