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
        setColorCells(table, value, column, row, c);

        return c;
    }

    // Imposta il colore e il bordo per le piazzole
    private void setColorCells(JTable table, Object value, int column, int row, Component c) {

        if(column != 0) {
            ((JLabel) c).setText("");
        }

        // Imposta il colore delle celle prenotate
        if(column != 0 && !Objects.equals(value.toString(), "0")){
            c.setBackground(TableConstants.CALENDARIO_PRENOTAZIONE_COLOR);

            // Imposta il bordo (con il colore del bordo che dipende dal tipo di piazzola
            if (table.getValueAt(row, 0).toString().contains("StarBox")) {
                setCellBorder(table, column, row, (JLabel) c, TableConstants.CALENDARIO_STARSBOX_COLOR);
            } else if (table.getValueAt(row, 0).toString().contains("Margherita")) {
                setCellBorder(table, column, row, (JLabel) c, TableConstants.CALENDARIO_MARGHERITA_COLOR);
            } else if (table.getValueAt(row, 0).toString().contains("G")) {
                setCellBorder(table, column, row, (JLabel) c, TableConstants.CALENDARIO_G_COLOR);
            } else if (table.getValueAt(row, 0).toString().contains("BBQ")) {
                setCellBorder(table, column, row, (JLabel) c, TableConstants.CALENDARIO_BBQ_COLOR);
            } else {
                setCellBorder(table, column, row, (JLabel) c, Color.WHITE);
            }

            return;
        } else {
            c.setBackground(table.getBackground());

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

    // Imposta il bordo della cella a seconda della prenotazione
    private static void setCellBorder(JTable table, int column, int row, JLabel c, Color color) {
        boolean isPrenotazioneCell = !Objects.equals(table.getValueAt(row, column).toString(), "0");
        boolean isStartOfPrenotazione = isPrenotazioneCell &&
                ((column == 1) || !Objects.equals(table.getValueAt(row, column - 1).toString(), table.getValueAt(row, column).toString()));
        boolean isEndOfPrenotazione = isPrenotazioneCell &&
                ((column == table.getColumnCount() - 1) || !Objects.equals(table.getValueAt(row, column + 1).toString(), table.getValueAt(row, column).toString()));

        boolean isFirstDayOfPrenotazione = isStartOfPrenotazione;
        boolean isLastDayOfPrenotazione = isEndOfPrenotazione;

        if (isStartOfPrenotazione) {
            if(row >= 1)
                isFirstDayOfPrenotazione = !Objects.equals(table.getValueAt(row - 1, column).toString(), table.getValueAt(row, column).toString());
            else
                isFirstDayOfPrenotazione = true;
        }

        if (isEndOfPrenotazione) {
            isLastDayOfPrenotazione = !Objects.equals(table.getValueAt(row + 1, column).toString(), table.getValueAt(row, column).toString());
        }

        if (isFirstDayOfPrenotazione && isLastDayOfPrenotazione) {
            c.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, color));
        } else if (isFirstDayOfPrenotazione) {
            c.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 0, color));
        } else if (isLastDayOfPrenotazione) {
            c.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, color));
        } else {
            c.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, color));
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
