package renderers_calendario;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CalendarioCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Imposta altezza righe in base al numero di righe
        setTableRowHeight(table);

        // La prima colonna (diversa dalle piazzole) deve essere sempre evidenziata
        if (column == 1) {
            c.setBackground(new Color(52, 201, 235));
        } else if (table.getColumnName(column).contains("(S)") || table.getColumnName(column).contains("(D)")){
            c.setBackground(Color.lightGray);
        } else {
            c.setBackground(table.getBackground());
        }

        //TODO: colorare in modo diverso la colonna delle piazzole
        if (column == 0) {
            c.setBackground(table.getBackground());
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
