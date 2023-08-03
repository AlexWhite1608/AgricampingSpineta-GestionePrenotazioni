package controllers;

import data_access.Gateway;
import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class TablePrenotazioniController {

    // Lista dei nomi delle prenotazioni
    static ArrayList<String> listaNomi = new ArrayList<>();

    private JTable tblPrenotazioni;
    private JComboBox cbFiltro;

    public Gateway getGateway() {
        return gateway;
    }

    private final Gateway gateway;

    public TablePrenotazioniController(JTable tblPrenotazioni) {
        this.tblPrenotazioni = tblPrenotazioni;
        this.gateway  = new Gateway();
    }

    // Mostra la visualizzazione iniziale della tabella (con il filtro dell'anno)
    public JTable initView(JComboBox cbFiltro, String filterQuery) throws SQLException {
        this.cbFiltro = cbFiltro;

        // Ottiene il valore selezionato nella comboBox
        String selectedFilterYear = Objects.requireNonNull(cbFiltro.getSelectedItem()).toString();

        if(filterQuery == null){
            String initialQuery = "";
            if(Objects.equals(selectedFilterYear, "Tutto")) {
                initialQuery = "SELECT * FROM Prenotazioni";
            } else {
                initialQuery = String.format("SELECT * FROM Prenotazioni WHERE substr(Arrivo, 7, 4) = '%s' OR substr(Partenza, 7, 4) = '%s'", selectedFilterYear, selectedFilterYear);
            }

            ResultSet resultSet = this.gateway.execSelectQuery(initialQuery);
            tblPrenotazioni = new JTable(gateway.buildPrenotazioniTableModel(resultSet));
        } else {
            ResultSet resultSet = this.gateway.execSelectQuery(filterQuery);
            tblPrenotazioni = new JTable(gateway.buildPrenotazioniTableModel(resultSet));
        }

        return tblPrenotazioni;
    }

    // Ricarica la visualizzazione della tabella
    public void refreshTable(JTable tabellaPrenotazioni) {
        try {

            // Lancia evento di modifica della tabella
            tabellaPrenotazioni.setModel(this.initView(cbFiltro, null).getModel());
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

    // Ricarica la visualizzazione della tabella (con applicazione del filtro)
    public void refreshTable(JTable tabellaPrenotazioni, String filterQuery) {
        try {

            // Lancia evento di modifica della tabella
            tabellaPrenotazioni.setModel(this.initView(cbFiltro, filterQuery).getModel());
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

    //TODO: FAI COME PER IL CALENDARIO, SPOSTA I RENDERER IN UNA CLASSE SPECIFICA!

    // Renderer estetica celle
    public DefaultTableCellRenderer createCellRenderer() {
        return new DefaultTableCellRenderer() {
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
                if (isSelected) {
                    c.setBackground(TableConstants.SELECTION_COLOR);
                }

                // Verifica che nella cella dell'acconto sia sempre visualizzato il simbolo €
                if (column == 4) {
                    Object cellValue = table.getValueAt(row, 4);
                    if (cellValue != null) {
                        String valoreAcconto = cellValue.toString();
                        if (!valoreAcconto.isEmpty()) {
                            if (!valoreAcconto.contains("€")) {
                                String nuovoValoreAcconto = "€ " + valoreAcconto;
                                table.setValueAt(nuovoValoreAcconto, row, 4);
                            }
                        }
                    }
                }

                // Imposta il bordo di selezione della cella (sia click sinistro che destro)
                if (isSelected && table.getSelectedColumn() == column && table.getSelectedRow() == row) {
                    setBorder(BorderFactory.createLineBorder(TableConstants.BORDER_CELL_SELECTED));
                } else {
                    setBorder(BorderFactory.createEmptyBorder());
                }

                // Ricavo le informazioni per l'acconto
                String acconto = (String) table.getModel().getValueAt(row, 5);
                String nome = (String) table.getModel().getValueAt(row, 4);
                String partenza = (String) table.getModel().getValueAt(row, 3);
                String arrivo = (String) table.getModel().getValueAt(row, 2);

                // Colora il testo dell'acconto (rosso -> non saldato, verde -> saldato)
                try {
                    if (column == 4 && !checkAccontoIsSaldato(nome, arrivo, partenza, acconto)) {
                        setForeground(Color.RED);
                    } else if (column == 4 && checkAccontoIsSaldato(nome, arrivo, partenza, acconto)) {
                        setForeground(TableConstants.ACCONTO_SALDATO_COLOR);
                    } else {
                        setForeground(Color.BLACK);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
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
                setFont(TableConstants.HEADER_FONT_PRENOTAZIONI);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setOpaque(false);

                return c;
            }
        };
    }

    // Ritorna tutti i nomi delle prenotazioni
    public static ArrayList<String> getAllNames() throws SQLException {
        ResultSet nomiRs = new Gateway().execSelectQuery("SELECT Nome FROM Prenotazioni");
        while (nomiRs.next()) {
            if(!listaNomi.contains(nomiRs.getString("Nome")))
                listaNomi.add(nomiRs.getString("Nome"));
        }
        nomiRs.close();

        return listaNomi;
    }

    // Modifica il valore dei nomi dopo l'aggiornamento
    public static void refreshAllNames() throws SQLException {
        ResultSet nomiRs = new Gateway().execSelectQuery("SELECT Nome FROM Prenotazioni");
        while (nomiRs.next()) {
            if(!listaNomi.contains(nomiRs.getString("Nome")))
                listaNomi.add(nomiRs.getString("Nome"));
        }
        nomiRs.close();
    }

    // Verifica se nella riga selezionata è presente o meno l'acconto per l'opzione SaldaAcconto nel popup
    public boolean isAcconto(Object value) {
        if (value instanceof String) {
            return value != null;
        }
        return false;
    }

    // Controlla dal db se l'acconto è saldato o meno
    private boolean checkAccontoIsSaldato(String... values) throws SQLException {
        String checkQuery = "SELECT Saldato FROM SaldoAcconti WHERE Nome = ? AND Arrivo = ? AND Partenza = ? AND Acconto = ?";

        ResultSet rs = new Gateway().execSelectQuery(checkQuery, values);
        if(rs.next()){
            String isSaldato = rs.getString("Saldato");
            rs.close();
            return Objects.equals(isSaldato, "saldato");
        } else {
            rs.close();
            return false;
        }
    }

    public static ArrayList<String> getListaNomi() {
        return listaNomi;
    }
}
