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
    public static DefaultCategoryDataset getDataset(String annoSelezionato) throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Map<String, Integer>> presenzeForMese = getPresenzeForMese();

        for(Map.Entry<String, Map<String, Integer>> entryAnni : presenzeForMese.entrySet()){
            String anno = entryAnni.getKey();
            for(Map.Entry<String, Integer> entryPresenze : entryAnni.getValue().entrySet()){
                String mese = entryPresenze.getKey();
                int presenze = entryPresenze.getValue();
            }
        }

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
            int persone = rs.getInt("Persone");
            String anno = rs.getString("mese").substring(3, 7);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate arrivo = LocalDate.parse(rs.getString("Arrivo"), formatter);
            LocalDate partenza = LocalDate.parse(rs.getString("Partenza"), formatter);

            while (!arrivo.isAfter(partenza)) {
                String meseCorrente = arrivo.format(DateTimeFormatter.ofPattern("MM/yyyy"));
                int giorniNelMese = arrivo.lengthOfMonth();
                int giorniMeseCorrente;

                if (arrivo.getMonth() == partenza.getMonth()) {
                    giorniMeseCorrente = Math.min(partenza.getDayOfMonth() - arrivo.getDayOfMonth(),
                            (int) ChronoUnit.DAYS.between(arrivo, partenza));
                } else {
                    giorniMeseCorrente = Math.min(giorniNelMese - arrivo.getDayOfMonth() + 1,
                            (int) ChronoUnit.DAYS.between(arrivo, partenza) + 1);
                }

                // Calcola le presenze per il mese corrente
                int presenzeMeseCorrente = giorniMeseCorrente * persone;

                presenzeMap
                        .computeIfAbsent(anno, k -> new HashMap<>())
                        .merge(meseCorrente, presenzeMeseCorrente, Integer::sum);

                arrivo = arrivo.plusMonths(1).withDayOfMonth(1);
            }
        }

        rs.close();

        return presenzeMap;
    }

}
