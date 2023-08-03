package renderers_calendario;

import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CalendarioCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Imposta altezza righe in base al numero di righe
        setTableRowHeight(table);

        // Centra le celle
        setHorizontalAlignment(HORIZONTAL);
        setVerticalAlignment(CENTER);

        // La prima colonna (diversa dalle piazzole) deve essere sempre evidenziata
        if (column == 1) {
            c.setBackground(TableConstants.CALENDARIO_FIRST_DAY_COLOR);
        } else if (table.getColumnName(column).contains("(S)") || table.getColumnName(column).contains("(D)")){
            c.setBackground(TableConstants.CALENDARIO_WEEKEND_COLOR);
        } else {
            c.setBackground(table.getBackground());
        }

        //TODO: colorare in modo diverso la colonna delle piazzole
        if (column == 0) {
            c.setBackground(table.getBackground());
            ((JLabel) c).setBorder(BorderFactory.createMatteBorder(0, 0, 0, TableConstants.SEPARATOR_BORDER_WIDTH, Color.BLACK));
        } else {
            ((JLabel) c).setBorder(null);
        }

        return c;
    }

    // Calcola l'altezza delle righe
    private void setTableRowHeight(JTable table) {
        int totalRows = table.getRowCount();
        int availableHeight = table.getParent().getHeight();
        int rowHeight = Math.max(1, availableHeight / totalRows);

        table.setRowHeight(rowHeight);
    }
}
