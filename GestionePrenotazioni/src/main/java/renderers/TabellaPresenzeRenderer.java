package renderers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TabellaPresenzeRenderer extends DefaultTableCellRenderer  {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Imposta altezza righe in base al numero di righe
        table.setRowHeight(20);

        // Centra le celle
        setHorizontalAlignment(HORIZONTAL);
        setVerticalAlignment(CENTER);

        return c;
    }
}
