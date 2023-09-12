package table_stats_controllers;

import datasets.DatasetMezziController;
import datasets.DatasetPresenzeController;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

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
                    result = mezzo + " (" + numMezzi + ")";
                }
            }

        }

        return result;
    }
}
