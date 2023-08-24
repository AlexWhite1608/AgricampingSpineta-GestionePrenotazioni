package renderers;

import data_access.Gateway;
import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

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

        // Imposta il colore della riga in base al valore di Arrivato/Partito
        String idPrenotazione = table.getModel().getValueAt(row, 0).toString();
        String statusColumn = (table.getColumnCount() == 8) ? "Partito" : "Arrivato";

        String getStatusQuery = "SELECT " + statusColumn + " FROM ArriviPartenze WHERE Id = ?";
        try {
            ResultSet rs = new Gateway().execSelectQuery(getStatusQuery, idPrenotazione);

            if (rs.next()) {
                String status = rs.getString(statusColumn);
                if (Objects.equals(status, "si")) {
                    c.setBackground(TableConstants.ARRIVI_PARTENZE_CONFERMA_COLOR);
                } else {
                    c.setBackground(table.getBackground());
                }
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return c;
    }
}
