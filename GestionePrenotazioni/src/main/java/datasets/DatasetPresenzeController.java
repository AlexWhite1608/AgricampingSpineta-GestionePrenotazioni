package datasets;

import data_access.Gateway;
import org.jfree.data.category.DefaultCategoryDataset;
import utils.TimeManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DatasetPresenzeController {

    // Ricava il dataset per il grafico
    public static DefaultCategoryDataset getPlotDataset(String annoSelezionato) throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Map<String, Integer>> presenzeForMese = getPresenzeForMese();

        // Ordina i mesi
        Map<String, Map<String, Integer>> presenzeForMeseOrdinate = TimeManager.orderMesiPresenzeMap(presenzeForMese);

        for(Map.Entry<String, Map<String, Integer>> entryAnni : presenzeForMeseOrdinate.entrySet()){
            String anno = entryAnni.getKey();

            if(Objects.equals(anno, annoSelezionato)) {
                for(Map.Entry<String, Integer> entryPresenze : entryAnni.getValue().entrySet()){
                    String mese = entryPresenze.getKey();
                    int presenze = entryPresenze.getValue();

                    dataset.addValue(presenze, "Mese", mese);
                }
            }
        }

        return dataset;
    }

    // Ricava il dataset per la tabella
    public static Map<String, Map<String, Integer>> getTableDataset() throws SQLException {
        return getPresenzeForMese();
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
//                String annoCorrente = arrivo.format(DateTimeFormatter.ofPattern("yyyy"));
//                String annoSuccessivo = arrivo.plusYears(1).format(DateTimeFormatter.ofPattern("yyyy"));

                presenzeMap
                        .computeIfAbsent(anno, k -> new HashMap<>())
                        .merge(meseCorrente, presenzeMeseCorrente, Integer::sum);

                //FIXME: quando c'è una prenotazione a cavallo tra due anni viene contata bene sul grafico ma non sulla tabella

                arrivo = arrivo.plusMonths(1).withDayOfMonth(1);
            }
        }

        rs.close();

        for (Map.Entry<String, Map<String, Integer>> yearEntry : presenzeMap.entrySet()) {
            Map<String, Integer> monthMap = yearEntry.getValue();
            yearEntry.setValue(invertMonthMapOrder(monthMap));
        }

        return presenzeMap;
    }

    // Serve per ordinare cronologicamente i mesi
    public static Map<String, Integer> invertMonthMapOrder(Map<String, Integer> monthMap) {
        LinkedHashMap<String, Integer> invertedMonthMap = new LinkedHashMap<>();
        List<String> monthKeys = new ArrayList<>(monthMap.keySet());

        Collections.reverse(monthKeys);

        for (String monthKey : monthKeys) {
            invertedMonthMap.put(monthKey, monthMap.get(monthKey));
        }

        return invertedMonthMap;
    }
}
