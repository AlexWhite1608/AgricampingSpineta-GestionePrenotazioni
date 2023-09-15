package renderers;

import utils.CalculatePercentage;
import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class TabellaFissaRenderer  extends DefaultTableCellRenderer {

    private String tipoTabella;

    public TabellaFissaRenderer(String tipoTabella) {
        this.tipoTabella = tipoTabella;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Imposta altezza righe in base al numero di righe
        if(Objects.equals(tipoTabella, "mesi"))
            table.setRowHeight(20);
        else if (Objects.equals(tipoTabella, "mezzi"))
            table.setRowHeight(25);
        else if(Objects.equals(tipoTabella, "nazioni"))
            table.setRowHeight(30);

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

        return c;
    }
}
