package renderers_calendario;

import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class CalendarioCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Imposta altezza righe in base al numero di righe
        setTableRowHeight(table);

        // Centra le celle
        setHorizontalAlignment(HORIZONTAL);
        setVerticalAlignment(CENTER);

        // Imposta il colore e il bordo per le piazzole e per le celle
        setColorCells(table, value, column, c);

        return c;
    }

    // Imposta il colore e il bordo per le piazzole
    private void setColorCells(JTable table, Object value, int column, Component c) {

//        boolean isPrenotazioneCell = Objects.equals(value.toString(), "1");
//        boolean isFirstCellOfPrenotazione = isPrenotazioneCell && column != 0 &&
//                (column == 1 ||
//                        (table.getValueAt(row, column - 1) != null &&
//                                !table.getValueAt(row, column - 1).toString().equals("1")));

        // Imposta il colore delle celle prenotate
        if(Objects.equals(value.toString(), "1")){
            c.setBackground(TableConstants.CALENDARIO_PRENOTAZIONE_COLOR);
            ((JLabel) c).setText("");
            return;
        } else if(Objects.equals(value.toString(), "0")){
            c.setBackground(table.getBackground());
            ((JLabel) c).setText("");
        }

        if (column == 0) {
            if (value.toString().contains("StarBox")) {
                c.setBackground(TableConstants.CALENDARIO_STARSBOX_COLOR);
            } else if (value.toString().contains("Margherita")) {
                c.setBackground(TableConstants.CALENDARIO_MARGHERITA_COLOR);
            } else if (value.toString().contains("G")) {
                c.setBackground(TableConstants.CALENDARIO_G_COLOR);
            } else if (value.toString().contains("BBQ")) {
                c.setBackground(TableConstants.CALENDARIO_BBQ_COLOR);
            } else {
                c.setBackground(table.getBackground());
            }

            // Imposta il bordo per la colonna delle Piazzole
            ((JLabel) c).setBorder(BorderFactory.createMatteBorder(0, 0, 0, TableConstants.SEPARATOR_BORDER_WIDTH, Color.BLACK));
        } else {
            ((JLabel) c).setBorder(null);

            // La colonna della data odierna deve essere sempre evidenziata
            if (column == 1) {
                c.setBackground(TableConstants.CALENDARIO_FIRST_DAY_COLOR);
            } else if (table.getColumnName(column).contains("(S)") || table.getColumnName(column).contains("(D)")) {
                c.setBackground(TableConstants.CALENDARIO_WEEKEND_COLOR);
            }
        }
    }

    // Calcola l'altezza delle righe
    private void setTableRowHeight(JTable table) {
        int totalRows = table.getRowCount();
        int availableHeight = table.getParent().getHeight();
        int rowHeight = Math.max(1, availableHeight / totalRows);

        table.setRowHeight(rowHeight);
    }
}
