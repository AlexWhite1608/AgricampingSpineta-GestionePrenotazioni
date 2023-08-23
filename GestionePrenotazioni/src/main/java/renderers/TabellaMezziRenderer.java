package renderers;

import utils.CalculatePercentageChange;
import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class TabellaMezziRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Imposta altezza righe in base al numero di righe
        table.setRowHeight(28);

        // Centra le celle
        setHorizontalAlignment(HORIZONTAL);
        setVerticalAlignment(CENTER);

        // Imposta la dimensione del font
        Font originalFont = c.getFont();
        Font newFont = originalFont;
        if(column == 0)
            newFont = originalFont.deriveFont(Font.BOLD, 15);
        else
            newFont = originalFont.deriveFont(Font.PLAIN, 15);

        c.setFont(newFont);

        // Imposta il bordo superiore sulla riga del totale
        if(row == table.getRowCount() - 1){
            ((JLabel) c).setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
            c.setBackground(TableConstants.ALTERNATE_CELL_COLOR);

        } else {
            c.setBackground(table.getBackground());
        }

        // Inserisce la percentuale rispetto all'anno precedente
        if (table.getColumnCount() > 2 && column > 1) {
            // Ottieni il valore della cella dell'anno precedente
            Object previousYearValue = table.getValueAt(row, column - 1);

            if (previousYearValue != null && previousYearValue instanceof Number) {
                int currentValue = ((Number) value).intValue();

                // Calcola la percentuale di variazione
                double percentageChange = CalculatePercentageChange.calculatePercentageChange((int) previousYearValue, currentValue);

                // Formatta la percentuale con due decimali
                String formattedPercentage = String.format("%.2f%%", percentageChange);
                String combinedValue = String.format("%s (%s)", value, formattedPercentage);

                ((JLabel) c).setText(combinedValue);

                // Imposta il colore del testo in base alla percentuale di variazione
                if (percentageChange > 0) {
                    c.setBackground(TableConstants.TABELLA_PERCENTUALE_CRESCITA);
                } else if (percentageChange < 0) {
                    c.setBackground(TableConstants.TABELLA_PERCENTUALE_DECRESCITA);
                } else {
                    c.setBackground(c.getBackground());
                }
            }
        }

        return c;
    }


}
