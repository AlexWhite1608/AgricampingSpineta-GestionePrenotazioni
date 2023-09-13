package table_stats_controllers;

import datasets.DatasetMezziController;
import datasets.DatasetPresenzeController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

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
    public static void setTableModelNazioni(JTable table, String anno, String mese) throws SQLException {

        // Imposta le colonne
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Nazione");
        columnNames.add("Presenze");

        // Imposta i dati del modello
        Map<String, Map<String, Map<String, Integer>>> dataset = DatasetPresenzeController.getPresenzeForMeseAndNazione();

//        DefaultTableModel model = new DefaultTableModel(data, columnNames){
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        table.setModel(model);

    }

}
