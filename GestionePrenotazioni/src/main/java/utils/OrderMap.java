package utils;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

// Ordina le mappe per le statistiche
public class OrderMap {

    public static Map<String, Map<String, Integer>> orderAnniPresenzeMap(Map<String, Map<String, Integer>> mapToConvert) {
        Map<String, Map<String, Integer>> datasetConMesiItaliani = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : mapToConvert.entrySet()) {
            String anno = entry.getKey();
            Map<String, Integer> mesiPresenze = entry.getValue();
            Map<String, Integer> mesiPresenzeItaliani = new HashMap<>();

            for (Map.Entry<String, Integer> mesePresenzeEntry : mesiPresenze.entrySet()) {
                String meseAnno = mesePresenzeEntry.getKey();
                Integer presenze = mesePresenzeEntry.getValue();

                // Converte il mese dall formato "mm/yyyy" a Month
                String[] parts = meseAnno.split("/");
                int mese = Integer.parseInt(parts[0]);
                Month month = Month.of(mese);

                // Ottieni il nome del mese in italiano con la prima lettera maiuscola
                String nomeMese = month.getDisplayName(TextStyle.FULL, Locale.ITALIAN);
                String meseMaiuscola = nomeMese.substring(0, 1).toUpperCase() + nomeMese.substring(1);

                // Aggiungi alla mappa con i mesi in italiano
                mesiPresenzeItaliani.put(meseMaiuscola, presenze);
            }

            // Aggiungi alla mappa principale con i mesi in italiano
            datasetConMesiItaliani.put(anno, mesiPresenzeItaliani);
        }

        return datasetConMesiItaliani;
    }

    public static Map<String, Map<String, Integer>> orderMezziMap(Map<String, Map<String, Integer>> mapToConvert) {
        Map<String, Map<String, Integer>> sortedDataset = new HashMap<>(); // TreeMap per ordinare gli anni

        for (Map.Entry<String, Map<String, Integer>> entry : mapToConvert.entrySet()) {
            String anno = entry.getKey();
            Map<String, Integer> mezzi = entry.getValue();
            Map<String, Integer> veicoliPerAnno = sortedDataset.computeIfAbsent(anno, k -> new HashMap<>());

            for (Map.Entry<String, Integer> mesePresenzeEntry : mezzi.entrySet()) {
                String mezzo = mesePresenzeEntry.getKey();
                Integer numeroVeicoli = mesePresenzeEntry.getValue();

                // Aggiungi alla mappa con i veicoli per l'anno
                veicoliPerAnno.put(mezzo, numeroVeicoli);
            }
        }

        return sortedDataset;
    }

    public static Map<String, Map<String, Integer>> orderMesiPresenzeMap(Map<String, Map<String, Integer>> dataset) {
        Map<String, Map<String, Integer>> datasetOrdinato = new HashMap<>();

        for (String outerKey : dataset.keySet()) {
            Map<String, Integer> innerMap = dataset.get(outerKey);

            TreeMap<String, Integer> orderedMap = new TreeMap<>(new MonthComparator());
            for (String mese : innerMap.keySet()) {
                int numMese = Integer.parseInt(mese.substring(0, 2));
                orderedMap.put(mese, numMese);
            }

            Map<String, Integer> innerMapOrdinato = new LinkedHashMap<>();
            orderedMap.forEach((mese, numMese) -> innerMapOrdinato.put(mese, innerMap.get(mese)));
            datasetOrdinato.put(outerKey, innerMapOrdinato);
        }

        return datasetOrdinato;
    }
}
