package stats_controllers;

import data_access.Gateway;
import org.jfree.data.category.DefaultCategoryDataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class DatasetPresenzeController {

    // Ricava il dataset per il grafico
    //TODO: devi considerare l'anno proveniente dalla combobox
    public static DefaultCategoryDataset getDataset() throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Map<String, Integer>> presenzeForMese = getPresenzeForMese();

        return dataset;
    }

    // Ricava le presenze per ogni mese dal database
    private static Map<String, Map<String, Integer>> getPresenzeForMese() throws SQLException {
        Map<String, Map<String, Integer>> presenzeMap = new HashMap<>();

        String query = "SELECT " +
                "substr(Arrivo, 4, 7) AS mese, " +
                "Persone, Arrivo, Partenza " +
                "FROM Prenotazioni " +
                "ORDER BY mese";

        ResultSet rs = new Gateway().execSelectQuery(query);

        while (rs.next()) {
            String mese = rs.getString("mese");
            int persone = rs.getInt("Persone");
            String anno = mese.substring(3, 7);

            // Calcolo il numero di notti per poi ottenere le presenze come: numNotti * persone
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate arrivo = LocalDate.parse(rs.getString("Arrivo"), formatter);
            LocalDate partenza = LocalDate.parse(rs.getString("Partenza"), formatter);
            int numNotti = Integer.parseInt(String.valueOf(ChronoUnit.DAYS.between(arrivo, partenza)));

            int presenze = numNotti * persone;

            presenzeMap.computeIfAbsent(anno, k -> new HashMap<>())
                    .merge(mese, presenze, Integer::sum);
        }

        return presenzeMap;
    }
}
