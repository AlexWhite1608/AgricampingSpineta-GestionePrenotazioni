package stats_controllers;

import data_access.Gateway;
import org.jfree.data.category.DefaultCategoryDataset;
import utils.TimeManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DatasetMezziController {

    public static Map<String, Map<String, Integer>> getTableDataset() throws SQLException {
        return getCountMezzi();
    }

    // Ricava il numero di mezzi per ciascun anno
    private static Map<String, Map<String, Integer>> getCountMezzi() throws SQLException {
        Map<String, Map<String, Integer>> mezziMap = new HashMap<>();

        String query = "SELECT substr(Arrivo, 4, 7) AS Mese, Mezzo, COUNT(*) AS NumeroMezzi " +
                "FROM Prenotazioni " +
                "GROUP BY Mese, Mezzo;";

        ResultSet rs = new Gateway().execSelectQuery(query);

        while (rs.next()) {

            int numeroMezzi = rs.getInt("NumeroMezzi");
            String anno = rs.getString("Mese").substring(3, 7);
            String mezzo = rs.getString("Mezzo");

            mezziMap
                    .computeIfAbsent(anno, k -> new HashMap<>())
                    .merge(mezzo, numeroMezzi, Integer::sum);
        }

        rs.close();

        return mezziMap;
    }

    public static DefaultCategoryDataset getPlotDataset(String annoSelezionato) throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Map<String, Integer>> numeroMezzi = getCountMezzi();

        for(Map.Entry<String, Map<String, Integer>> entryAnni : numeroMezzi.entrySet()){
            String anno = entryAnni.getKey();

            if(Objects.equals(anno, annoSelezionato)) {
                for(Map.Entry<String, Integer> entryMezzi : entryAnni.getValue().entrySet()){
                    String mezzo = entryMezzi.getKey();
                    int numMezzi = entryMezzi.getValue();

                    dataset.addValue(numMezzi, "Mezzo", mezzo);
                }
            }
        }

        return dataset;
    }

}
