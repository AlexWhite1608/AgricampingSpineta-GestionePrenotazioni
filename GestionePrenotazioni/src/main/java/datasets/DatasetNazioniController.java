package datasets;

import data_access.Gateway;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatasetNazioniController {

    public static Map<String, Map<String, Integer>> getTableDataset() throws SQLException {
        return getCountNazioni();
    }

    // Ricava il numero di nazioni per ciascun anno
    private static Map<String, Map<String, Integer>> getCountNazioni() throws SQLException {
        Map<String, Map<String, Integer>> nazioniMap = new HashMap<>();

        String query = "SELECT " +
                "    substr(Arrivo, 4, 7) AS Mese, " +
                "    Nazione, " +
                "    SUM(Persone) AS totale_persone " +
                "    FROM " +
                "     Prenotazioni " +
                "    GROUP BY " +
                "     Mese, Nazione " +
                "    ORDER BY " +
                "     Mese, Nazione;";

        ResultSet rs = new Gateway().execSelectQuery(query);

        while (rs.next()) {

            int totalePersone = rs.getInt("totale_persone");
            String anno = rs.getString("Mese").substring(3, 7);
            String nazione = rs.getString("Nazione");

            nazioniMap
                    .computeIfAbsent(anno, k -> new HashMap<>())
                    .merge(nazione, totalePersone, Integer::sum);
        }

        rs.close();

        return nazioniMap;
    }
}
