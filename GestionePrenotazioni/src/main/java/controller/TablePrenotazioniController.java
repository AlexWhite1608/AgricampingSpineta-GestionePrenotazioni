package controller;

import data_access.Gateway;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class TablePrenotazioniController {

    // Costanti per i renderer della tabella
    private final Font CELL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Color ALTERNATE_CELL_COLOR = new Color(220, 232, 234);
    private final Color SELECTION_COLOR = new Color(255, 255, 102);
    private final Color HEADER_BACKGROUND = Color.LIGHT_GRAY;

    private JTable tblPrenotazioni;
    private JComboBox cbFiltro;
    private final Gateway gateway;

    public TablePrenotazioniController(JTable tblPrenotazioni) {
        this.tblPrenotazioni = tblPrenotazioni;
        this.gateway  = new Gateway();
    }

    // Mostra la visualizzazione iniziale della tabella (con il filtro dell'anno)
    public JTable initView(JComboBox cbFiltro) throws SQLException {
        this.cbFiltro = cbFiltro;

        // Ottiene il valore selezionato nella comboBox
        String selectedFilterYear = Objects.requireNonNull(cbFiltro.getSelectedItem()).toString();

        String initialQuery = "";
        if(Objects.equals(selectedFilterYear, "Tutto")) {
            initialQuery = "SELECT * FROM Prenotazioni";
        } else {
            initialQuery = String.format("SELECT * FROM Prenotazioni WHERE substr(Arrivo, 7, 4) = '%s' OR substr(Partenza, 7, 4) = '%s'", selectedFilterYear, selectedFilterYear);
        }

        ResultSet resultSet = this.gateway.execSelectQuery(initialQuery);
        tblPrenotazioni = new JTable(gateway.buildCustomTableModel(resultSet));

        return tblPrenotazioni;
    }

    // Ricarica la visualizzazione della tabella
    public void refreshTable(JTable tabellaPrenotazioni) {
        try {

            // Lancia evento di modifica della tabella
            tabellaPrenotazioni.setModel(this.initView(cbFiltro).getModel());
            ((AbstractTableModel) tabellaPrenotazioni.getModel()).fireTableDataChanged();

            // Riassegna tutti i renderer
            for(int columnIndex = 0; columnIndex < tabellaPrenotazioni.getColumnCount(); columnIndex++) {
                tabellaPrenotazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(this.createCellRenderer());
            }
            tabellaPrenotazioni.getTableHeader().setDefaultRenderer(this.createHeaderRenderer());

            // Rimuove la colonna id
            tabellaPrenotazioni.removeColumn(tabellaPrenotazioni.getColumnModel().getColumn(0));

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Renderer estetica celle
    public DefaultTableCellRenderer createCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(CELL_FONT);

                // Colora le righe alternativamente
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(ALTERNATE_CELL_COLOR);
                }

                // Colora di giallo la riga selezionata
                if(isSelected){
                    c.setBackground(SELECTION_COLOR);
                }

                return c;
            }
        };
    }

    // Renderer estetica header
    public DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(HEADER_FONT);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setBackground(HEADER_BACKGROUND);

                return c;
            }
        };
    }
}
