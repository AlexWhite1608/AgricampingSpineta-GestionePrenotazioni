package renderers;

import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ArriviPartenzeRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(TableConstants.CELL_FONT);

        // Colora le righe alternativamente
        if (row % 2 == 0) {
            c.setBackground(Color.WHITE);
        } else {
            c.setBackground(TableConstants.ALTERNATE_CELL_COLOR);
        }

        // Colora di giallo la riga selezionata
        //FIXME: NON FUNZIONA
        if (isSelected) {
            c.setBackground(TableConstants.SELECTION_COLOR);
        }

        return c;
    }
}
