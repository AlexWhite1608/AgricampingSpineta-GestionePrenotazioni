package table_stats_controllers;

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

                // Controlla se questo mese ha più presenze delle presenze massime attuali
                if (presenze > presenzeMassime) {
                    presenzeMassime = presenze;
                    result = mese + "-" + anno + " (" + presenze + ")";
                }
            }
        }

        return result;
    }
}
