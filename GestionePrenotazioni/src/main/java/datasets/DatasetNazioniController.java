package datasets;

import data_access.Gateway;
import org.jfree.data.general.DefaultPieDataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DatasetNazioniController {

    // Ricava il dataset per la tabella
    public static Map<String, Map<String, Integer>> getTableDataset() throws SQLException {
        return getCountNazioni();
    }

    // Ricava il dataset per il grafico
    public static DefaultPieDataset getPlotDataset(String annoSelezionato) throws SQLException {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, Map<String, Integer>> numeroPresenzeNazioni = getCountNazioni();

        for(Map.Entry<String, Map<String, Integer>> entryAnni : numeroPresenzeNazioni.entrySet()){
            String anno = entryAnni.getKey();

            if(Objects.equals(anno, annoSelezionato)) {
                for(Map.Entry<String, Integer> entryNazioni : entryAnni.getValue().entrySet()){
                    String nazione = entryNazioni.getKey();
                    int numPresenze = entryNazioni.getValue();

                    if(!Objects.equals(nazione, ""))
                        dataset.setValue(nazione, numPresenze);
                }
            }
        }

        return dataset;
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
