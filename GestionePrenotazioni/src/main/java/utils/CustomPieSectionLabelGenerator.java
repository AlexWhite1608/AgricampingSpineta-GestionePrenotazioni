package utils;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.text.AttributedString;

// Genera le labels per il grafico a torta con le percentuali
public class CustomPieSectionLabelGenerator implements PieSectionLabelGenerator {
    private final DefaultPieDataset dataset;

    public CustomPieSectionLabelGenerator(DefaultPieDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public String generateSectionLabel(PieDataset pieDataset, Comparable comparable) {
        int sectionIndex = dataset.getIndex(comparable);
        double sectionValue = dataset.getValue(sectionIndex).doubleValue();

        // Calcola la somma totale dei valori nel dataset
        double total = 0;
        for (int i = 0; i < dataset.getItemCount(); i++) {
            total += dataset.getValue(i).doubleValue();
        }

        double percentage = (sectionValue / total) * 100;

        return comparable.toString() + ": " + String.format("%.2f", percentage) + "%";
    }

    @Override
    public AttributedString generateAttributedSectionLabel(PieDataset pieDataset, Comparable comparable) {
        return null;
    }
}
