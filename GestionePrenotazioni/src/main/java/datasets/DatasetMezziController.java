package datasets;

import data_access.Gateway;
import org.jfree.data.category.DefaultCategoryDataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DatasetMezziController {

    // Ricava il dataset per la tabella
    public static Map<String, Map<String, Integer>> getTableDataset() throws SQLException {
        return getCountMezzi();
    }

    // Ricava il dataset per il grafico
    public static DefaultCategoryDataset getPlotDataset(String annoSelezionato) throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Map<String, Integer>> numeroMezzi = getCountMezzi();

        for(Map.Entry<String, Map<String, Integer>> entryAnni : numeroMezzi.entrySet()){
            String anno = entryAnni.getKey();

            if(Objects.equals(anno, annoSelezionato)) {
                for(Map.Entry<String, Integer> entryMezzi : entryAnni.getValue().entrySet()){
                    String mezzo = entryMezzi.getKey();
                    int numMezzi = entryMezzi.getValue();

                    if(!Objects.equals(mezzo, ""))
                        dataset.addValue(numMezzi, "Mezzo", mezzo);
                }
            }
        }

        return dataset;
    }

    // Ricava il numero di mezzi per ciascun anno
    public static Map<String, Map<String, Integer>> getCountMezzi() throws SQLException {
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

    // Ottiene il numero di veicoli per ciascuna nazione
    public static Map<String, Map<String, Map<String, Map<String, Integer>>>> getNumeroVeicoliNazione() throws SQLException {

        Map<String, Map<String, Map<String, Map<String, Integer>>>> numeroMezziPerNazione = new HashMap<>();

        String query = "SELECT " +
                "substr(Arrivo, 4, 7) AS mese, " +
                "Nazione, COUNT(Mezzo) AS NumeroMezzi, Arrivo, Partenza, Mezzo " +
                "FROM Prenotazioni " +
                "GROUP BY mese, Mezzo, Nazione " +
                "ORDER BY mese, Mezzo, Nazione";

        ResultSet rs = new Gateway().execSelectQuery(query);

        while (rs.next()) {
            int numeroMezzi = rs.getInt("NumeroMezzi");
            String anno = rs.getString("mese").substring(3, 7);
            String nazione = rs.getString("Nazione");
            String mezzo = rs.getString("Mezzo");

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

                // Utilizza la nazione come chiave aggiuntiva
                numeroMezziPerNazione
                        .computeIfAbsent(anno, k -> new HashMap<>())
                        .computeIfAbsent(nazione, k -> new HashMap<>())
                        .computeIfAbsent(meseCorrente, k -> new HashMap<>())
                        .merge(mezzo, numeroMezzi, Integer::sum);

                arrivo = arrivo.plusMonths(1).withDayOfMonth(1);
            }
        }

        rs.close();

        return numeroMezziPerNazione;
    }
}
