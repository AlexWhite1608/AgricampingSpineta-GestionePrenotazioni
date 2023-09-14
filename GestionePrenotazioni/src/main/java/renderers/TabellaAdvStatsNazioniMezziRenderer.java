package renderers;

import utils.CalculatePercentage;
import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TabellaAdvStatsNazioniMezziRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Imposta altezza righe in base al numero di righe
        table.setRowHeight(30);

        // Centra le celle
        setHorizontalAlignment(HORIZONTAL);
        setVerticalAlignment(CENTER);

        // Imposta la dimensione del font
        Font originalFont = c.getFont();
        Font newFont = originalFont;
        newFont = originalFont.deriveFont(Font.BOLD, 13);
        c.setFont(newFont);

        // Ottengo la somma di tutti i mezzi
        int totalMezzi = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            int numeroMezzi = (int) table.getValueAt(i, 1);
            totalMezzi += numeroMezzi;
        }

        // Inserisce la percentuale rispetto al totale dei veicoli
        if (totalMezzi > 1 && column == 1) {
            int mezziValue = (int) table.getValueAt(row, 1);
            double percentage = (double) mezziValue / totalMezzi * 100;

            // Formatta il valore della cella per includere la percentuale
            String formattedValue = String.format("%d (%.2f%%)", mezziValue, percentage);
            ((JLabel) c).setText(formattedValue);

        } else if (totalMezzi == 1 && column == 1) {
            int mezziValue = (int) table.getValueAt(row, 1);
            String formattedValue = String.format("%d (%.2f%%)", mezziValue, 100.00);
            ((JLabel) c).setText(formattedValue);
        }


        return c;
    }

}
