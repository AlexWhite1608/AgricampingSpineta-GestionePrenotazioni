package stats_controllers;

import data_access.Gateway;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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

}